-- =====================================================
-- 多级隧道转发功能数据库升级脚本
-- 版本: v1.1.0
-- 日期: 2025-01-XX
-- 功能: 添加多级隧道转发支持 (type=4)
-- =====================================================

-- 1. 修改tunnel表，添加多级节点配置字段
ALTER TABLE `tunnel` 
ADD COLUMN `hop_nodes` TEXT DEFAULT NULL COMMENT '多级节点配置(JSON格式)，存储节点链路信息' AFTER `ss_config`;

-- 2. 更新表注释，说明新的隧道类型
ALTER TABLE `tunnel` 
MODIFY COLUMN `type` int(10) NOT NULL COMMENT '1-端口转发, 2-隧道转发, 3-端口复用, 4-多级隧道转发';

-- 3. 添加索引优化查询性能
ALTER TABLE `tunnel` 
ADD INDEX `idx_type` (`type`);

-- =====================================================
-- 数据结构说明
-- =====================================================
-- hop_nodes字段存储JSON格式的多级节点配置，示例：
-- [
--   {
--     "nodeId": 1,
--     "nodeName": "节点A",
--     "nodeIp": "192.168.1.1",
--     "port": 8080,
--     "protocol": "tls",
--     "interfaceName": "eth0"
--   },
--   {
--     "nodeId": 2,
--     "nodeName": "节点B",
--     "nodeIp": "192.168.1.2",
--     "port": 8081,
--     "protocol": "tls",
--     "interfaceName": null
--   }
-- ]
-- 
-- 流量路径: 用户 -> 入口节点 -> 节点A -> 节点B -> ... -> 出口节点 -> 目标服务器
-- =====================================================

-- 验证修改
SELECT 
    COLUMN_NAME, 
    COLUMN_TYPE, 
    COLUMN_COMMENT 
FROM 
    INFORMATION_SCHEMA.COLUMNS 
WHERE 
    TABLE_SCHEMA = DATABASE() 
    AND TABLE_NAME = 'tunnel' 
    AND COLUMN_NAME IN ('type', 'hop_nodes');

