package cn.surveyking.server.domain.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.HashMap;
import java.util.Map;

@Data
@EqualsAndHashCode(callSuper = true)
public class AnalyticsQueryRequest extends PageQuery {

	private String scriptId;

	private Map<String, Object> params = new HashMap<>();

}
