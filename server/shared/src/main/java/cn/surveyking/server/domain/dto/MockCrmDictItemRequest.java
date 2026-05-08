package cn.surveyking.server.domain.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * CRM mock 字典项维护请求。
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class MockCrmDictItemRequest extends PageQuery {

	private String id;

	private String dictCode;

	private String label;

	private String value;

	private String parentValue;

	private Integer itemLevel;

	private Integer itemOrder;

	private Boolean enabled;

}
