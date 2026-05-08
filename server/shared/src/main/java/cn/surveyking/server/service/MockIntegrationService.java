package cn.surveyking.server.service;

import cn.surveyking.server.core.common.PaginationResponse;
import cn.surveyking.server.domain.dto.MockCrmCustomerRequest;
import cn.surveyking.server.domain.dto.MockCrmCustomerView;
import cn.surveyking.server.domain.dto.MockCrmDictItemRequest;
import cn.surveyking.server.domain.dto.MockCrmDictItemView;
import cn.surveyking.server.domain.dto.MockIntegrationConfigRequest;
import cn.surveyking.server.domain.dto.MockIntegrationConfigView;

import java.util.List;

/**
 * 外部集成 mock 管理服务。
 */
public interface MockIntegrationService {

	String INTEGRATION_CRM = "crm";

	boolean isMockEnabled(String integrationCode);

	List<MockIntegrationConfigView> listConfigs();

	MockIntegrationConfigView upsertConfig(MockIntegrationConfigRequest request);

	PaginationResponse<MockCrmCustomerView> listCrmCustomers(MockCrmCustomerRequest request);

	MockCrmCustomerView saveCrmCustomer(MockCrmCustomerRequest request);

	void deleteCrmCustomer(MockCrmCustomerRequest request);

	PaginationResponse<MockCrmDictItemView> listCrmDictItems(MockCrmDictItemRequest request);

	MockCrmDictItemView saveCrmDictItem(MockCrmDictItemRequest request);

	void deleteCrmDictItem(MockCrmDictItemRequest request);

}
