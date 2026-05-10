package cn.surveyking.server.service;

import cn.surveyking.server.domain.dto.AnalyticsQueryRequest;
import cn.surveyking.server.domain.dto.AnalyticsQueryResult;
import cn.surveyking.server.domain.dto.AnalyticsScriptView;

import java.util.List;

public interface AnalyticsService {

	List<AnalyticsScriptView> listScripts();

	AnalyticsQueryResult query(AnalyticsQueryRequest request);

}
