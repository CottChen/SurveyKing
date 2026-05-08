package cn.surveyking.server.impl;

import cn.surveyking.server.core.common.PaginationResponse;
import cn.surveyking.server.domain.dto.MockCrmCustomerRequest;
import cn.surveyking.server.domain.dto.MockCrmCustomerView;
import cn.surveyking.server.domain.dto.MockCrmDictItemRequest;
import cn.surveyking.server.domain.dto.MockCrmDictItemView;
import cn.surveyking.server.domain.dto.MockIntegrationConfigRequest;
import cn.surveyking.server.domain.dto.MockIntegrationConfigView;
import cn.surveyking.server.domain.model.MockCrmCustomer;
import cn.surveyking.server.domain.model.MockCrmDictItem;
import cn.surveyking.server.domain.model.MockIntegrationConfig;
import cn.surveyking.server.mapper.MockCrmCustomerMapper;
import cn.surveyking.server.mapper.MockCrmDictItemMapper;
import cn.surveyking.server.mapper.MockIntegrationConfigMapper;
import cn.surveyking.server.service.MockIntegrationService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.ValidationException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 外部集成 mock 管理服务实现。
 */
@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class MockIntegrationServiceImpl implements MockIntegrationService {

	private final MockIntegrationConfigMapper configMapper;

	private final MockCrmCustomerMapper customerMapper;

	private final MockCrmDictItemMapper dictItemMapper;

	@Override
	public boolean isMockEnabled(String integrationCode) {
		MockIntegrationConfig config = findConfig(integrationCode);
		return config == null || Boolean.TRUE.equals(config.getMockEnabled());
	}

	@Override
	public List<MockIntegrationConfigView> listConfigs() {
		return configMapper.selectList(Wrappers.<MockIntegrationConfig>lambdaQuery()
				.orderByAsc(MockIntegrationConfig::getIntegrationCode)).stream().map(this::toConfigView)
				.collect(Collectors.toList());
	}

	@Override
	public MockIntegrationConfigView upsertConfig(MockIntegrationConfigRequest request) {
		if (request == null || StringUtils.isBlank(request.getIntegrationCode())) {
			throw new ValidationException("集成编码不能为空");
		}
		MockIntegrationConfig config = findConfig(request.getIntegrationCode());
		if (config == null) {
			config = new MockIntegrationConfig();
			config.setIntegrationCode(request.getIntegrationCode().trim());
		}
		config.setMockEnabled(Boolean.TRUE.equals(request.getMockEnabled()));
		config.setRemark(request.getRemark());
		if (StringUtils.isBlank(config.getId())) {
			configMapper.insert(config);
		} else {
			configMapper.updateById(config);
		}
		return toConfigView(config);
	}

	@Override
	public PaginationResponse<MockCrmCustomerView> listCrmCustomers(MockCrmCustomerRequest request) {
		MockCrmCustomerRequest safeRequest = request == null ? new MockCrmCustomerRequest() : request;
		Page<MockCrmCustomer> page = customerMapper.selectPage(
				new Page<>(safeRequest.getCurrent(), safeRequest.getPageSize()),
				Wrappers.<MockCrmCustomer>lambdaQuery()
						.eq(StringUtils.isNotBlank(safeRequest.getProjectId()), MockCrmCustomer::getProjectId,
								safeRequest.getProjectId())
						.like(StringUtils.isNotBlank(safeRequest.getCustomerName()), MockCrmCustomer::getCustomerName,
								safeRequest.getCustomerName())
						.eq(StringUtils.isNotBlank(safeRequest.getMobile()), MockCrmCustomer::getMobile,
								safeRequest.getMobile())
						.eq(safeRequest.getEnabled() != null, MockCrmCustomer::getEnabled, safeRequest.getEnabled())
						.orderByAsc(MockCrmCustomer::getCustomerId));
		return new PaginationResponse<>(page.getTotal(),
				page.getRecords().stream().map(this::toCustomerView).collect(Collectors.toList()));
	}

	@Override
	public MockCrmCustomerView saveCrmCustomer(MockCrmCustomerRequest request) {
		if (request == null || StringUtils.isBlank(request.getCustomerId())
				|| StringUtils.isBlank(request.getCustomerName()) || StringUtils.isBlank(request.getMobile())) {
			throw new ValidationException("客户编号、姓名和手机号不能为空");
		}
		MockCrmCustomer customer = StringUtils.isBlank(request.getId()) ? new MockCrmCustomer()
				: customerMapper.selectById(request.getId());
		if (customer == null) {
			throw new ValidationException("mock 客户不存在");
		}
		customer.setProjectId(request.getProjectId());
		customer.setCustomerId(request.getCustomerId().trim());
		customer.setCustomerName(request.getCustomerName().trim());
		customer.setMobile(request.getMobile().trim());
		customer.setCredentialNo(request.getCredentialNo());
		customer.setCustomerLevel(request.getCustomerLevel());
		customer.setProductInterest(request.getProductInterest());
		customer.setEnabled(request.getEnabled() == null || Boolean.TRUE.equals(request.getEnabled()));
		if (StringUtils.isBlank(customer.getId())) {
			customerMapper.insert(customer);
		} else {
			customerMapper.updateById(customer);
		}
		return toCustomerView(customer);
	}

	@Override
	public void deleteCrmCustomer(MockCrmCustomerRequest request) {
		if (request == null || StringUtils.isBlank(request.getId())) {
			return;
		}
		customerMapper.deleteBatchIds(Collections.singletonList(request.getId()));
	}

	@Override
	public PaginationResponse<MockCrmDictItemView> listCrmDictItems(MockCrmDictItemRequest request) {
		MockCrmDictItemRequest safeRequest = request == null ? new MockCrmDictItemRequest() : request;
		Page<MockCrmDictItem> page = dictItemMapper.selectPage(
				new Page<>(safeRequest.getCurrent(), safeRequest.getPageSize()),
				Wrappers.<MockCrmDictItem>lambdaQuery()
						.eq(StringUtils.isNotBlank(safeRequest.getDictCode()), MockCrmDictItem::getDictCode,
								safeRequest.getDictCode())
						.like(StringUtils.isNotBlank(safeRequest.getLabel()), MockCrmDictItem::getLabel,
								safeRequest.getLabel())
						.eq(safeRequest.getEnabled() != null, MockCrmDictItem::getEnabled, safeRequest.getEnabled())
						.orderByAsc(MockCrmDictItem::getDictCode).orderByAsc(MockCrmDictItem::getItemOrder));
		return new PaginationResponse<>(page.getTotal(),
				page.getRecords().stream().map(this::toDictItemView).collect(Collectors.toList()));
	}

	@Override
	public MockCrmDictItemView saveCrmDictItem(MockCrmDictItemRequest request) {
		if (request == null || StringUtils.isBlank(request.getDictCode()) || StringUtils.isBlank(request.getLabel())
				|| StringUtils.isBlank(request.getValue())) {
			throw new ValidationException("字典编码、名称和值不能为空");
		}
		MockCrmDictItem item = StringUtils.isBlank(request.getId()) ? new MockCrmDictItem()
				: dictItemMapper.selectById(request.getId());
		if (item == null) {
			throw new ValidationException("mock 字典项不存在");
		}
		item.setDictCode(request.getDictCode().trim());
		item.setLabel(request.getLabel().trim());
		item.setValue(request.getValue().trim());
		item.setParentValue(request.getParentValue());
		item.setItemLevel(request.getItemLevel());
		item.setItemOrder(request.getItemOrder());
		item.setEnabled(request.getEnabled() == null || Boolean.TRUE.equals(request.getEnabled()));
		if (StringUtils.isBlank(item.getId())) {
			dictItemMapper.insert(item);
		} else {
			dictItemMapper.updateById(item);
		}
		return toDictItemView(item);
	}

	@Override
	public void deleteCrmDictItem(MockCrmDictItemRequest request) {
		if (request == null || StringUtils.isBlank(request.getId())) {
			return;
		}
		dictItemMapper.deleteBatchIds(Collections.singletonList(request.getId()));
	}

	private MockIntegrationConfig findConfig(String integrationCode) {
		if (StringUtils.isBlank(integrationCode)) {
			return null;
		}
		return configMapper.selectOne(Wrappers.<MockIntegrationConfig>lambdaQuery()
				.eq(MockIntegrationConfig::getIntegrationCode, integrationCode.trim()).last("limit 1"));
	}

	private MockIntegrationConfigView toConfigView(MockIntegrationConfig config) {
		MockIntegrationConfigView view = new MockIntegrationConfigView();
		view.setIntegrationCode(config.getIntegrationCode());
		view.setMockEnabled(config.getMockEnabled());
		view.setRemark(config.getRemark());
		return view;
	}

	private MockCrmCustomerView toCustomerView(MockCrmCustomer customer) {
		MockCrmCustomerView view = new MockCrmCustomerView();
		view.setId(customer.getId());
		view.setProjectId(customer.getProjectId());
		view.setCustomerId(customer.getCustomerId());
		view.setCustomerName(customer.getCustomerName());
		view.setMobile(customer.getMobile());
		view.setCredentialNo(customer.getCredentialNo());
		view.setCustomerLevel(customer.getCustomerLevel());
		view.setProductInterest(customer.getProductInterest());
		view.setEnabled(customer.getEnabled());
		return view;
	}

	private MockCrmDictItemView toDictItemView(MockCrmDictItem item) {
		MockCrmDictItemView view = new MockCrmDictItemView();
		view.setId(item.getId());
		view.setDictCode(item.getDictCode());
		view.setLabel(item.getLabel());
		view.setValue(item.getValue());
		view.setParentValue(item.getParentValue());
		view.setItemLevel(item.getItemLevel());
		view.setItemOrder(item.getItemOrder());
		view.setEnabled(item.getEnabled());
		return view;
	}

}
