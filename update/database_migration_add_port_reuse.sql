-- 数据库迁移脚本：添加端口复用功能
-- 执行日期：2025-10-30
-- 说明：为tunnel表添加ss_config字段，并更新type字段注释
-- 兼容 MySQL 5.7

-- 1. 检查并添加ss_config字段
SET @dbname = DATABASE();
SET @tablename = 'tunnel';
SET @columnname = 'ss_config';
SET @preparedStatement = (SELECT IF(
  (
    SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
    WHERE
      (table_name = @tablename)
      AND (table_schema = @dbname)
      AND (column_name = @columnname)
  ) > 0,
  'SELECT ''Column already exists'' AS status;',
  'ALTER TABLE `tunnel` ADD COLUMN `ss_config` text DEFAULT NULL COMMENT ''SS节点配置(端口复用时使用)'' AFTER `interface_name`;'
));
PREPARE alterIfNotExists FROM @preparedStatement;
EXECUTE alterIfNotExists;
DEALLOCATE PREPARE alterIfNotExists;

-- 2. 更新type字段注释
ALTER TABLE `tunnel`
MODIFY COLUMN `type` int(10) NOT NULL COMMENT '1-端口转发, 2-隧道转发, 3-端口复用';

-- 迁移完成
SELECT 'Database migration completed successfully!' AS status;

