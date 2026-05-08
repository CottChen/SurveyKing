package cn.surveyking.server.domain.dto;

import lombok.Data;

/**
 * 外部集成 mock 开关视图。
 */
@Data
public class MockIntegrationConfigView {

	private String integrationCode;

	private Boolean mockEnabled;

	private String remark;

}
