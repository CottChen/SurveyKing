package cn.surveyking.server.analytics;

import cn.surveyking.server.domain.dto.AnalyticsColumn;
import cn.surveyking.server.domain.dto.AnalyticsDrillTarget;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class AnalyticsScriptDefinition {

	private String id;

	private String name;

	private String description;

	private String category;

	private String sql;

	private String countSql;

	private int maxPageSize = 500;

	private List<AnalyticsColumn> columns = new ArrayList<>();

	private List<AnalyticsDrillTarget> drillTargets = new ArrayList<>();

}
