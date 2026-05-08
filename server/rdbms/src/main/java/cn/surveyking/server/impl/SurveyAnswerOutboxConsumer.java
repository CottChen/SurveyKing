package cn.surveyking.server.impl;

import cn.surveyking.server.domain.model.SurveyAnswerOutboxEvent;
import cn.surveyking.server.mapper.SurveyAnswerOutboxEventMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * 本地 outbox 消费器。当前先落 JSONL，后续可替换为 Webhook、CRM 或 lake sink。
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SurveyAnswerOutboxConsumer {

	private final SurveyAnswerOutboxEventMapper outboxEventMapper;

	private final ObjectMapper objectMapper;

	@Value("${surveyking.outbox.consumer.enabled:true}")
	private boolean enabled;

	@Value("${surveyking.outbox.consumer.batch-size:20}")
	private int batchSize;

	@Value("${surveyking.outbox.consumer.retry-delay-ms:60000}")
	private long retryDelayMs;

	@Value("${surveyking.outbox.consumer.local-jsonl.enabled:true}")
	private boolean localJsonlEnabled;

	@Value("${surveyking.outbox.consumer.local-jsonl.path:./.local/surveyking/outbox/survey-answer-events.jsonl}")
	private String localJsonlPath;

	@Scheduled(fixedDelayString = "${surveyking.outbox.consumer.fixed-delay-ms:5000}")
	public void consumeReadyEvents() {
		if (!enabled) {
			return;
		}
		List<SurveyAnswerOutboxEvent> events = outboxEventMapper.listReadyToConsume(new Date(), batchSize);
		for (SurveyAnswerOutboxEvent event : events) {
			consumeOne(event);
		}
	}

	@Transactional
	public void consumeOne(SurveyAnswerOutboxEvent event) {
		event.setStatus(SurveyAnswerOutboxEvent.STATUS_PROCESSING);
		event.setErrorMessage(null);
		outboxEventMapper.updateById(event);
		try {
			writeLocalJsonl(event);
			event.setStatus(SurveyAnswerOutboxEvent.STATUS_SENT);
			event.setNextRetryAt(null);
			event.setErrorMessage(null);
			outboxEventMapper.updateById(event);
		} catch (Exception ex) {
			int retryCount = event.getRetryCount() == null ? 0 : event.getRetryCount();
			event.setStatus(SurveyAnswerOutboxEvent.STATUS_FAILED);
			event.setRetryCount(retryCount + 1);
			event.setNextRetryAt(new Date(System.currentTimeMillis() + retryDelayMs));
			event.setErrorMessage(trimErrorMessage(ex));
			outboxEventMapper.updateById(event);
			log.warn("failed to consume survey answer outbox event, id={}, eventKey={}", event.getId(),
					event.getEventKey(), ex);
		}
	}

	private void writeLocalJsonl(SurveyAnswerOutboxEvent event) throws IOException {
		if (!localJsonlEnabled) {
			return;
		}
		Path path = Paths.get(localJsonlPath);
		Path parent = path.getParent();
		if (parent != null) {
			Files.createDirectories(parent);
		}
		LinkedHashMap<String, Object> line = new LinkedHashMap<>();
		line.put("id", event.getId());
		line.put("eventType", event.getEventType());
		line.put("eventKey", event.getEventKey());
		line.put("projectId", event.getProjectId());
		line.put("answerId", event.getAnswerId());
		line.put("payload", event.getPayload());
		line.put("createAt", event.getCreateAt());
		Files.write(path, (objectMapper.writeValueAsString(line) + System.lineSeparator())
				.getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
	}

	private String trimErrorMessage(Exception ex) {
		String message = ex.getMessage();
		if (message == null) {
			message = ex.getClass().getName();
		}
		return message.length() > 2000 ? message.substring(0, 2000) : message;
	}

}
