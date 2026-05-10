package cn.surveyking.server.api;

import cn.surveyking.server.domain.dto.AnalyticsQueryRequest;
import cn.surveyking.server.domain.dto.AnalyticsQueryResult;
import cn.surveyking.server.domain.dto.AnalyticsScriptView;
import cn.surveyking.server.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/analytics")
@RequiredArgsConstructor
public class AnalyticsApi {

	private final AnalyticsService analyticsService;

	@GetMapping("/scripts")
	@PreAuthorize("hasAuthority('answer:list')")
	public List<AnalyticsScriptView> listScripts() {
		return analyticsService.listScripts();
	}

	@PostMapping("/query")
	@PreAuthorize("hasAuthority('answer:list')")
	public AnalyticsQueryResult query(@RequestBody AnalyticsQueryRequest request) {
		return analyticsService.query(request);
	}

}
