package cn.surveyking.server.domain.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class AnalyticsQueryResult {

	private String scriptId;

	private List<AnalyticsColumn> columns;

	private List<Map<String, Object>> rows;

	private long total;

	private int current;

	private int pageSize;

	private List<AnalyticsDrillTarget> drillTargets;

}
