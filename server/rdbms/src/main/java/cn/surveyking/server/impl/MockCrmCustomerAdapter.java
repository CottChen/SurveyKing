package cn.surveyking.server.impl;

import cn.surveyking.server.core.constant.ErrorCode;
import cn.surveyking.server.core.exception.ErrorCodeException;
import cn.surveyking.server.domain.dto.CrmCustomerVerifyRequest;
import cn.surveyking.server.domain.dto.CrmCustomerVerifyView;
import cn.surveyking.server.domain.dto.PublicDictRequest;
import cn.surveyking.server.domain.dto.PublicDictView;
import cn.surveyking.server.domain.model.MockCrmCustomer;
import cn.surveyking.server.domain.model.MockCrmDictItem;
import cn.surveyking.server.mapper.MockCrmCustomerMapper;
import cn.surveyking.server.mapper.MockCrmDictItemMapper;
import cn.surveyking.server.service.CrmCustomerAdapter;
import cn.surveyking.server.service.MockIntegrationService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.validation.ValidationException;

/**
 * 开发环境 CRM mock 适配器。
 */
@Service
@RequiredArgsConstructor
public class MockCrmCustomerAdapter implements CrmCustomerAdapter {

	private final MockIntegrationService mockIntegrationService;

	private final MockCrmCustomerMapper customerMapper;

	private final MockCrmDictItemMapper dictItemMapper;

	@Override
	public CrmCustomerVerifyView verifyCustomer(CrmCustomerVerifyRequest request) {
		if (!mockIntegrationService.isMockEnabled(MockIntegrationService.INTEGRATION_CRM)) {
			throw new ValidationException("真实 CRM 适配器未配置");
		}
		if (request == null || StringUtils.isBlank(request.getProjectId())
				|| StringUtils.isBlank(request.getCustomerName()) || StringUtils.isBlank(request.getMobile())) {
			throw new ErrorCodeException(ErrorCode.ValidationError);
		}
		Optional<MockCrmCustomer> match = customerMapper.selectList(Wrappers.<MockCrmCustomer>lambdaQuery()
				.eq(MockCrmCustomer::getEnabled, true)
				.eq(MockCrmCustomer::getCustomerName, request.getCustomerName().trim())
				.eq(MockCrmCustomer::getMobile, request.getMobile().trim())
				.and(wrapper -> wrapper.isNull(MockCrmCustomer::getProjectId).or()
						.eq(MockCrmCustomer::getProjectId, "").or()
						.eq(MockCrmCustomer::getProjectId, request.getProjectId()))
				.orderByDesc(MockCrmCustomer::getProjectId))
				.stream()
				.filter(customer -> StringUtils.isBlank(request.getCredentialNo())
						|| StringUtils.isBlank(customer.getCredentialNo())
						|| request.getCredentialNo().trim().equals(customer.getCredentialNo()))
				.findFirst();
		if (!match.isPresent()) {
			throw new ErrorCodeException(ErrorCode.ValidationError);
		}
		MockCrmCustomer customer = match.get();
		CrmCustomerVerifyView view = new CrmCustomerVerifyView();
		view.setVerified(true);
		view.setCustomerId(customer.getCustomerId());
		view.setCustomerName(customer.getCustomerName());
		view.setMobile(customer.getMobile());
		view.setMessage("mock CRM 校验通过");
		view.setVerifyToken(buildVerifyToken(request.getProjectId(), customer.getCustomerId()));
		view.getAttributes().put("customerLevel", customer.getCustomerLevel());
		view.getAttributes().put("productInterest", customer.getProductInterest());
		view.getAttributes().put("source", "mock-crm");
		return view;
	}

	@Override
	public List<PublicDictView> loadDict(PublicDictRequest request) {
		if (!mockIntegrationService.isMockEnabled(MockIntegrationService.INTEGRATION_CRM)) {
			throw new ValidationException("真实 CRM 字典适配器未配置");
		}
		if (request == null || StringUtils.isBlank(request.getDictCode())) {
			throw new ErrorCodeException(ErrorCode.ValidationError);
		}
		return dictItemMapper.selectList(Wrappers.<MockCrmDictItem>lambdaQuery()
				.eq(MockCrmDictItem::getEnabled, true)
				.eq(MockCrmDictItem::getDictCode, request.getDictCode())
				.eq(request.getCascaderLevel() != null, MockCrmDictItem::getItemLevel, request.getCascaderLevel())
				.eq(request.getParentValue() != null, MockCrmDictItem::getParentValue, request.getParentValue())
				.and(StringUtils.isNotBlank(request.getSearch()),
						wrapper -> wrapper.like(MockCrmDictItem::getLabel, request.getSearch()).or()
								.like(MockCrmDictItem::getValue, request.getSearch()))
				.orderByAsc(MockCrmDictItem::getItemOrder))
				.stream()
				.limit(request.getLimit() == null ? 50 : request.getLimit())
				.map(this::toPublicDictView)
				.collect(Collectors.toList());
	}

	private String buildVerifyToken(String projectId, String customerId) {
		String tokenSource = projectId + ":" + customerId + ":mock-crm";
		return "MOCK-CRM-" + UUID.nameUUIDFromBytes(tokenSource.getBytes(StandardCharsets.UTF_8));
	}

	private PublicDictView toPublicDictView(MockCrmDictItem item) {
		PublicDictView view = new PublicDictView();
		view.setLabel(item.getLabel());
		view.setValue(item.getValue());
		return view;
	}

}
