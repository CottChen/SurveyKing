package cn.surveyking.server.domain.dto;

import lombok.Data;

/**
 * 外部集成 mock 开关维护请求。
 */
@Data
public class MockIntegrationConfigRequest {

	private String integrationCode;

	private Boolean mockEnabled;

	private String remark;

}
