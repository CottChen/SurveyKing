package cn.surveyking.server.domain.model;

import cn.surveyking.server.core.model.BaseModel;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * CRM mock 字典项数据。
 */
@Data
@TableName("t_mock_crm_dict_item")
@EqualsAndHashCode(callSuper = false)
public class MockCrmDictItem extends BaseModel {

	private String dictCode;

	private String label;

	private String value;

	private String parentValue;

	private Integer itemLevel;

	private Integer itemOrder;

	private Boolean enabled;

}
