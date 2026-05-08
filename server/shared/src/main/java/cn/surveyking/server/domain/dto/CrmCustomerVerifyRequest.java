package cn.surveyking.server.domain.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * CRM 客户实名校验请求。
 */
@Data
public class CrmCustomerVerifyRequest {

	/**
	 * 问卷 ID，用于后续按问卷配置切换实名规则。
	 */
	@NotBlank(message = "问卷 ID 不能为空")
	private String projectId;

	@NotBlank(message = "客户姓名不能为空")
	private String customerName;

	@NotBlank(message = "手机号不能为空")
	private String mobile;

	/**
	 * 证件号、会员号等二级校验信息，mock 环境允许为空。
	 */
	private String credentialNo;

}
