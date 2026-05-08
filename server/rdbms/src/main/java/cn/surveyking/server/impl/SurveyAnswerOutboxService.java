package cn.surveyking.server.impl;

import cn.surveyking.server.domain.dto.AnswerView;
import cn.surveyking.server.domain.dto.ProjectView;
import cn.surveyking.server.domain.model.SurveyAnswerOutboxEvent;
import cn.surveyking.server.mapper.SurveyAnswerOutboxEventMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;

/**
 * 只负责写入 outbox，不在答卷提交事务中同步调用外部系统。
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SurveyAnswerOutboxService {

	private final SurveyAnswerOutboxEventMapper outboxEventMapper;

	public void recordAnswerSubmitted(ProjectView project, AnswerView answer) {
		if (project == null || answer == null || answer.getId() == null) {
			return;
		}
		String eventKey = SurveyAnswerOutboxEvent.TYPE_ANSWER_SUBMITTED + ":" + answer.getId();
		Long existing = outboxEventMapper.selectCount(Wrappers.<SurveyAnswerOutboxEvent>lambdaQuery()
				.eq(SurveyAnswerOutboxEvent::getEventKey, eventKey));
		if (existing != null && existing > 0) {
			return;
		}

		SurveyAnswerOutboxEvent event = new SurveyAnswerOutboxEvent();
		event.setEventType(SurveyAnswerOutboxEvent.TYPE_ANSWER_SUBMITTED);
		event.setEventKey(eventKey);
		event.setProjectId(project.getId());
		event.setAnswerId(answer.getId());
		event.setStatus(SurveyAnswerOutboxEvent.STATUS_PENDING);
		event.setRetryCount(0);
		event.setPayload(buildPayload(project, answer));
		try {
			outboxEventMapper.insert(event);
		} catch (DuplicateKeyException ignored) {
			log.debug("answer submitted outbox event already exists, eventKey={}", eventKey);
		}
	}

	private LinkedHashMap<String, Object> buildPayload(ProjectView project, AnswerView answer) {
		LinkedHashMap<String, Object> payload = new LinkedHashMap<>();
		payload.put("eventType", SurveyAnswerOutboxEvent.TYPE_ANSWER_SUBMITTED);
		payload.put("projectId", project.getId());
		payload.put("projectName", project.getName());
		payload.put("projectMode", project.getMode());
		payload.put("answerId", answer.getId());
		payload.put("answer", answer.getAnswer());
		payload.put("survey", answer.getSurvey() != null ? answer.getSurvey() : project.getSurvey());
		payload.put("metaInfo", answer.getMetaInfo());
		payload.put("examScore", answer.getExamScore());
		payload.put("examInfo", answer.getExamInfo());
		payload.put("tempSave", answer.getTempSave());
		payload.put("createAt", answer.getCreateAt());
		payload.put("createBy", answer.getCreateBy());
		return payload;
	}

}
