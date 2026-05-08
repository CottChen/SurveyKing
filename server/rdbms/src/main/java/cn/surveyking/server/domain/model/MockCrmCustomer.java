package cn.surveyking.server.domain.model;

import cn.surveyking.server.core.model.BaseModel;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * CRM mock 客户数据。
 */
@Data
@TableName("t_mock_crm_customer")
@EqualsAndHashCode(callSuper = false)
public class MockCrmCustomer extends BaseModel {

	private String projectId;

	private String customerId;

	private String customerName;

	private String mobile;

	private String credentialNo;

	private String customerLevel;

	private String productInterest;

	private Boolean enabled;

}
