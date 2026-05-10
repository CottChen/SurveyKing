package cn.surveyking.server.domain.dto;

import lombok.Data;

import java.util.Map;

@Data
public class AnalyticsDrillTarget {

	private String id;

	private String label;

	private String scriptId;

	private Map<String, String> paramMapping;

}
