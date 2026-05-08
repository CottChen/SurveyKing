package cn.surveyking.server.impl;

import cn.surveyking.server.core.constant.ErrorCode;
import cn.surveyking.server.core.exception.ErrorCodeException;
import cn.surveyking.server.domain.dto.CrmCustomerVerifyRequest;
import cn.surveyking.server.domain.dto.CrmCustomerVerifyView;
import cn.surveyking.server.domain.dto.PublicDictRequest;
import cn.surveyking.server.domain.dto.PublicDictView;
import cn.surveyking.server.service.CrmCustomerAdapter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 开发环境 CRM mock 适配器。
 */
@Service
public class MockCrmCustomerAdapter implements CrmCustomerAdapter {

	private final List<MockCustomer> customers = Arrays.asList(
			new MockCustomer("MOCK-CUST-001", "张三", "13800000000", "VIP", "service"),
			new MockCustomer("MOCK-CUST-002", "李四", "13900000000", "普通", "insurance"),
			new MockCustomer("MOCK-CUST-003", "王五", "13700000000", "潜在", "car")
	);

	private final Map<String, List<PublicDictView>> dicts = buildDicts();

	@Override
	public CrmCustomerVerifyView verifyCustomer(CrmCustomerVerifyRequest request) {
		if (request == null || StringUtils.isBlank(request.getProjectId())
				|| StringUtils.isBlank(request.getCustomerName()) || StringUtils.isBlank(request.getMobile())) {
			throw new ErrorCodeException(ErrorCode.ValidationError);
		}
		Optional<MockCustomer> match = customers.stream()
				.filter(customer -> customer.customerName.equals(request.getCustomerName().trim()))
				.filter(customer -> customer.mobile.equals(request.getMobile().trim()))
				.findFirst();
		if (!match.isPresent()) {
			throw new ErrorCodeException(ErrorCode.ValidationError);
		}
		MockCustomer customer = match.get();
		CrmCustomerVerifyView view = new CrmCustomerVerifyView();
		view.setVerified(true);
		view.setCustomerId(customer.customerId);
		view.setCustomerName(customer.customerName);
		view.setMobile(customer.mobile);
		view.setMessage("mock CRM 校验通过");
		view.setVerifyToken(buildVerifyToken(request.getProjectId(), customer.customerId));
		view.getAttributes().put("customerLevel", customer.customerLevel);
		view.getAttributes().put("productInterest", customer.productInterest);
		view.getAttributes().put("source", "mock-crm");
		return view;
	}

	@Override
	public List<PublicDictView> loadDict(PublicDictRequest request) {
		if (request == null || StringUtils.isBlank(request.getDictCode())) {
			throw new ErrorCodeException(ErrorCode.ValidationError);
		}
		List<PublicDictView> values = dicts.getOrDefault(request.getDictCode(), new ArrayList<>());
		return values.stream()
				.filter(item -> StringUtils.isBlank(request.getSearch())
						|| StringUtils.containsIgnoreCase(item.getLabel(), request.getSearch())
						|| StringUtils.containsIgnoreCase(item.getValue(), request.getSearch()))
				.limit(request.getLimit() == null ? 50 : request.getLimit())
				.collect(Collectors.toList());
	}

	private String buildVerifyToken(String projectId, String customerId) {
		String tokenSource = projectId + ":" + customerId + ":mock-crm";
		return "MOCK-CRM-" + UUID.nameUUIDFromBytes(tokenSource.getBytes(StandardCharsets.UTF_8));
	}

	private Map<String, List<PublicDictView>> buildDicts() {
		Map<String, List<PublicDictView>> result = new LinkedHashMap<>();
		result.put("crm.customerLevel", Arrays.asList(dictItem("VIP", "VIP"), dictItem("普通", "NORMAL"),
				dictItem("潜在", "POTENTIAL")));
		result.put("crm.productInterest", Arrays.asList(dictItem("车辆服务", "car"),
				dictItem("保险续保", "insurance"), dictItem("售后服务", "service")));
		return result;
	}

	private PublicDictView dictItem(String label, String value) {
		PublicDictView view = new PublicDictView();
		view.setLabel(label);
		view.setValue(value);
		return view;
	}

	private static class MockCustomer {

		private final String customerId;

		private final String customerName;

		private final String mobile;

		private final String customerLevel;

		private final String productInterest;

		private MockCustomer(String customerId, String customerName, String mobile, String customerLevel,
				String productInterest) {
			this.customerId = customerId;
			this.customerName = customerName;
			this.mobile = mobile;
			this.customerLevel = customerLevel;
			this.productInterest = productInterest;
		}

	}

}
