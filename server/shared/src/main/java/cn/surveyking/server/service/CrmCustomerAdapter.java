package cn.surveyking.server.service;

import cn.surveyking.server.domain.dto.CrmCustomerVerifyRequest;
import cn.surveyking.server.domain.dto.CrmCustomerVerifyView;
import cn.surveyking.server.domain.dto.PublicDictRequest;
import cn.surveyking.server.domain.dto.PublicDictView;

import java.util.List;

/**
 * CRM 客户信息适配器。
 */
public interface CrmCustomerAdapter {

	CrmCustomerVerifyView verifyCustomer(CrmCustomerVerifyRequest request);

	List<PublicDictView> loadDict(PublicDictRequest request);

}
