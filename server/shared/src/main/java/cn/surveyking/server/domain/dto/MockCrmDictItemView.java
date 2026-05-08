package cn.surveyking.server.domain.dto;

import lombok.Data;

/**
 * CRM mock 字典项视图。
 */
@Data
public class MockCrmDictItemView {

	private String id;

	private String dictCode;

	private String label;

	private String value;

	private String parentValue;

	private Integer itemLevel;

	private Integer itemOrder;

	private Boolean enabled;

}
