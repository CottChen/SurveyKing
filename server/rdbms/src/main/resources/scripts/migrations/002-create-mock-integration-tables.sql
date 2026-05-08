CREATE TABLE IF NOT EXISTS `t_mock_integration_config` (
  `id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
  `integration_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '外部集成编码',
  `mock_enabled` tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否启用mock',
  `remark` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '备注',
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否删除',
  `create_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_by` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  `update_at` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_by` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_mock_integration_code` (`integration_code`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='外部集成mock开关配置';

CREATE TABLE IF NOT EXISTS `t_mock_crm_customer` (
  `id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
  `project_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '问卷项目ID，空表示通用',
  `customer_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT 'CRM客户ID',
  `customer_name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '客户姓名',
  `mobile` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '手机号',
  `credential_no` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '证件号或会员号',
  `customer_level` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '客户等级',
  `product_interest` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '产品意向',
  `enabled` tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否启用',
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否删除',
  `create_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_by` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  `update_at` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_by` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_mock_crm_customer_lookup` (`project_id`, `customer_name`, `mobile`) USING BTREE,
  KEY `idx_mock_crm_customer_id` (`customer_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='CRM mock客户数据';

CREATE TABLE IF NOT EXISTS `t_mock_crm_dict_item` (
  `id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
  `dict_code` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '字典编码',
  `label` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '显示名称',
  `value` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '字典值',
  `parent_value` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '父级字典值',
  `item_level` int DEFAULT NULL COMMENT '层级',
  `item_order` int DEFAULT NULL COMMENT '排序',
  `enabled` tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否启用',
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否删除',
  `create_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_by` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  `update_at` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_by` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_mock_crm_dict_code` (`dict_code`, `enabled`, `item_order`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='CRM mock字典项数据';

INSERT IGNORE INTO `t_mock_integration_config`
(`id`, `integration_code`, `mock_enabled`, `remark`, `is_deleted`, `create_at`)
VALUES
('mock-config-crm', 'crm', 1, '开发环境默认启用 CRM mock', 0, CURRENT_TIMESTAMP);

INSERT IGNORE INTO `t_mock_crm_customer`
(`id`, `project_id`, `customer_id`, `customer_name`, `mobile`, `credential_no`, `customer_level`, `product_interest`, `enabled`, `is_deleted`, `create_at`)
VALUES
('mock-crm-customer-001', NULL, 'MOCK-CUST-001', '张三', '13800000000', NULL, 'VIP', 'service', 1, 0, CURRENT_TIMESTAMP),
('mock-crm-customer-002', NULL, 'MOCK-CUST-002', '李四', '13900000000', NULL, 'NORMAL', 'insurance', 1, 0, CURRENT_TIMESTAMP),
('mock-crm-customer-003', NULL, 'MOCK-CUST-003', '王五', '13700000000', NULL, 'POTENTIAL', 'car', 1, 0, CURRENT_TIMESTAMP);

INSERT IGNORE INTO `t_mock_crm_dict_item`
(`id`, `dict_code`, `label`, `value`, `parent_value`, `item_level`, `item_order`, `enabled`, `is_deleted`, `create_at`)
VALUES
('mock-crm-dict-level-001', 'crm.customerLevel', 'VIP', 'VIP', NULL, 1, 1, 1, 0, CURRENT_TIMESTAMP),
('mock-crm-dict-level-002', 'crm.customerLevel', '普通', 'NORMAL', NULL, 1, 2, 1, 0, CURRENT_TIMESTAMP),
('mock-crm-dict-level-003', 'crm.customerLevel', '潜在', 'POTENTIAL', NULL, 1, 3, 1, 0, CURRENT_TIMESTAMP),
('mock-crm-dict-interest-001', 'crm.productInterest', '车辆服务', 'car', NULL, 1, 1, 1, 0, CURRENT_TIMESTAMP),
('mock-crm-dict-interest-002', 'crm.productInterest', '保险续保', 'insurance', NULL, 1, 2, 1, 0, CURRENT_TIMESTAMP),
('mock-crm-dict-interest-003', 'crm.productInterest', '售后服务', 'service', NULL, 1, 3, 1, 0, CURRENT_TIMESTAMP);
