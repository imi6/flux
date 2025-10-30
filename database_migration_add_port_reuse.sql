-- 数据库迁移脚本：添加端口复用功能
-- 执行日期：2025-10-30
-- 说明：为tunnel表添加ss_config字段，并更新type字段注释

-- 1. 添加ss_config字段（如果不存在）
ALTER TABLE `tunnel` 
ADD COLUMN IF NOT EXISTS `ss_config` text DEFAULT NULL COMMENT 'SS节点配置(端口复用时使用)' 
AFTER `interface_name`;

-- 2. 更新type字段注释
ALTER TABLE `tunnel` 
MODIFY COLUMN `type` int(10) NOT NULL COMMENT '1-端口转发, 2-隧道转发, 3-端口复用';

-- 迁移完成
SELECT 'Database migration completed successfully!' AS status;

