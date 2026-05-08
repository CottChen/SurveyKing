package cn.surveyking.server.domain.model;

import cn.surveyking.server.core.model.BaseModel;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 外部集成 mock 开关配置。
 */
@Data
@TableName("t_mock_integration_config")
@EqualsAndHashCode(callSuper = false)
public class MockIntegrationConfig extends BaseModel {

	private String integrationCode;

	private Boolean mockEnabled;

	private String remark;

}
