package cn.surveyking.server.mapper;

import cn.surveyking.server.domain.model.SurveyAnswerOutboxEvent;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;

import java.util.Date;
import java.util.List;

public interface SurveyAnswerOutboxEventMapper extends BaseMapper<SurveyAnswerOutboxEvent> {

	@Select({ "select * from t_survey_answer_outbox_event",
			"where is_deleted = 0",
			"and (status = 'pending' or (status = 'failed' and (next_retry_at is null or next_retry_at <= #{now})))",
			"order by create_at asc",
			"limit #{limit}" })
	@ResultMap("mybatis-plus_SurveyAnswerOutboxEvent")
	List<SurveyAnswerOutboxEvent> listReadyToConsume(@Param("now") Date now, @Param("limit") int limit);

}
