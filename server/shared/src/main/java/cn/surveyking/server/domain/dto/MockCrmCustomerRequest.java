package cn.surveyking.server.domain.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * CRM mock 客户数据维护请求。
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class MockCrmCustomerRequest extends PageQuery {

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
