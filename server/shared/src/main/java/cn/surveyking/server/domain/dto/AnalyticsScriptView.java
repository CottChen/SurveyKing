package cn.surveyking.server.domain.dto;

import lombok.Data;

import java.util.List;

@Data
public class AnalyticsScriptView {

	private String id;

	private String name;

	private String description;

	private String category;

	private List<AnalyticsColumn> columns;

	private List<AnalyticsDrillTarget> drillTargets;

}
