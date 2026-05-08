package cn.surveyking.server.domain.dto;

import lombok.Data;

/**
 * CRM mock 客户数据视图。
 */
@Data
public class MockCrmCustomerView {

	private String id;

	private String projectId;

	private String customerId;

	private String customerName;

	private String mobile;

	private String credentialNo;

	private String customerLevel;

	private String productInterest;

	private Boolean enabled;

}
