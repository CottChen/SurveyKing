package cn.surveyking.server.domain.model;

import cn.surveyking.server.core.model.BaseModel;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.JdbcType;

import java.util.Date;
import java.util.LinkedHashMap;

/**
 * 答卷事件 outbox，用于异步对接 CRM、湖仓和后续 Webhook。
 */
@Data
@TableName(value = "t_survey_answer_outbox_event", autoResultMap = true)
@EqualsAndHashCode(callSuper = false)
public class SurveyAnswerOutboxEvent extends BaseModel {

	public static final String TYPE_ANSWER_SUBMITTED = "SURVEY_ANSWER_SUBMITTED";

	public static final String STATUS_PENDING = "pending";

	private String eventType;

	private String eventKey;

	private String projectId;

	private String answerId;

	private String status;

	private Integer retryCount;

	private Date nextRetryAt;

	private String errorMessage;

	@TableField(typeHandler = JacksonTypeHandler.class, jdbcType = JdbcType.LONGVARCHAR)
	private LinkedHashMap<String, Object> payload;

}
