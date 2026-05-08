package cn.surveyking.server.api;

import cn.surveyking.server.core.common.PaginationResponse;
import cn.surveyking.server.domain.dto.MockCrmCustomerRequest;
import cn.surveyking.server.domain.dto.MockCrmCustomerView;
import cn.surveyking.server.domain.dto.MockCrmDictItemRequest;
import cn.surveyking.server.domain.dto.MockCrmDictItemView;
import cn.surveyking.server.domain.dto.MockIntegrationConfigRequest;
import cn.surveyking.server.domain.dto.MockIntegrationConfigView;
import cn.surveyking.server.service.MockIntegrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 外部集成 mock 管理接口。
 */
@RestController
@RequestMapping("${api.prefix}/mock")
@RequiredArgsConstructor
public class MockIntegrationApi {

	private final MockIntegrationService mockIntegrationService;

	@GetMapping("/config/list")
	public List<MockIntegrationConfigView> listConfigs() {
		return mockIntegrationService.listConfigs();
	}

	@PostMapping("/config/save")
	public MockIntegrationConfigView saveConfig(@RequestBody MockIntegrationConfigRequest request) {
		return mockIntegrationService.upsertConfig(request);
	}

	@RequestMapping("/crm/customer/list")
	public PaginationResponse<MockCrmCustomerView> listCrmCustomers(MockCrmCustomerRequest request) {
		return mockIntegrationService.listCrmCustomers(request);
	}

	@PostMapping("/crm/customer/save")
	public MockCrmCustomerView saveCrmCustomer(@RequestBody MockCrmCustomerRequest request) {
		return mockIntegrationService.saveCrmCustomer(request);
	}

	@PostMapping("/crm/customer/delete")
	public void deleteCrmCustomer(@RequestBody MockCrmCustomerRequest request) {
		mockIntegrationService.deleteCrmCustomer(request);
	}

	@RequestMapping("/crm/dict/list")
	public PaginationResponse<MockCrmDictItemView> listCrmDictItems(MockCrmDictItemRequest request) {
		return mockIntegrationService.listCrmDictItems(request);
	}

	@PostMapping("/crm/dict/save")
	public MockCrmDictItemView saveCrmDictItem(@RequestBody MockCrmDictItemRequest request) {
		return mockIntegrationService.saveCrmDictItem(request);
	}

	@PostMapping("/crm/dict/delete")
	public void deleteCrmDictItem(@RequestBody MockCrmDictItemRequest request) {
		mockIntegrationService.deleteCrmDictItem(request);
	}

}
