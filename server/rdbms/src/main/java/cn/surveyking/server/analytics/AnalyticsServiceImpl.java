package cn.surveyking.server.analytics;

import cn.surveyking.server.domain.dto.AnalyticsQueryRequest;
import cn.surveyking.server.domain.dto.AnalyticsQueryResult;
import cn.surveyking.server.domain.dto.AnalyticsScriptView;
import cn.surveyking.server.service.AnalyticsService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class AnalyticsServiceImpl implements AnalyticsService {

	private final AnalyticsScriptRepository scriptRepository;

	private final NamedParameterJdbcTemplate analyticsJdbcTemplate;

	public AnalyticsServiceImpl(AnalyticsScriptRepository scriptRepository,
			@Qualifier("analyticsJdbcTemplate") NamedParameterJdbcTemplate analyticsJdbcTemplate) {
		this.scriptRepository = scriptRepository;
		this.analyticsJdbcTemplate = analyticsJdbcTemplate;
	}

	@Override
	public List<AnalyticsScriptView> listScripts() {
		return scriptRepository.list().stream().map(this::toView).collect(Collectors.toList());
	}

	@Override
	public AnalyticsQueryResult query(AnalyticsQueryRequest request) {
		AnalyticsScriptDefinition script = scriptRepository.get(request.getScriptId());
		int current = Math.max(request.getCurrent(), 1);
		int pageSize = Math.max(1, Math.min(request.getPageSize(), script.getMaxPageSize()));
		int offset = (current - 1) * pageSize;

		Map<String, Object> params = new HashMap<>();
		if (request.getParams() != null) {
			params.putAll(request.getParams());
		}
		params.put("__limit", pageSize);
		params.put("__offset", offset);

		String pageSql = "select * from (" + script.getSql() + ") analytics_result limit :__limit offset :__offset";
		List<Map<String, Object>> rows = analyticsJdbcTemplate.queryForList(pageSql, new MapSqlParameterSource(params));

		long total = rows.size();
		if (StringUtils.isNotBlank(script.getCountSql())) {
			validateReadonlySql(script.getCountSql(), script.getId() + ".countSql");
			Number count = analyticsJdbcTemplate.queryForObject(script.getCountSql(), new MapSqlParameterSource(params),
					Number.class);
			total = count == null ? 0 : count.longValue();
		}

		AnalyticsQueryResult result = new AnalyticsQueryResult();
		result.setScriptId(script.getId());
		result.setColumns(script.getColumns());
		result.setRows(rows);
		result.setTotal(total);
		result.setCurrent(current);
		result.setPageSize(pageSize);
		result.setDrillTargets(script.getDrillTargets());
		return result;
	}

	private AnalyticsScriptView toView(AnalyticsScriptDefinition definition) {
		AnalyticsScriptView view = new AnalyticsScriptView();
		view.setId(definition.getId());
		view.setName(definition.getName());
		view.setDescription(definition.getDescription());
		view.setCategory(definition.getCategory());
		view.setColumns(definition.getColumns());
		view.setDrillTargets(definition.getDrillTargets());
		return view;
	}

	private void validateReadonlySql(String sql, String id) {
		String normalizedSql = sql.trim().toLowerCase();
		if (!(normalizedSql.startsWith("select") || normalizedSql.startsWith("with")) || normalizedSql.contains(";")) {
			throw new IllegalArgumentException("分析脚本只允许单条 SELECT/WITH 查询：" + id);
		}
	}

}
