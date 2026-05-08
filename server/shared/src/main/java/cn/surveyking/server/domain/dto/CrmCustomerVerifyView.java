package cn.surveyking.server.domain.dto;

import lombok.Data;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * CRM 客户实名校验结果。
 */
@Data
public class CrmCustomerVerifyView {

	private Boolean verified;

	private String customerId;

	private String customerName;

	private String mobile;

	private String message;

	/**
	 * 供答卷端随提交一起回传的实名会话标识。
	 */
	private String verifyToken;

	/**
	 * mock CRM 返回的客户扩展属性。
	 */
	private Map<String, Object> attributes = new LinkedHashMap<>();

}
