# Flux 多级隧道转发功能 - 升级指南

## � 快速升级

### Docker部署（推荐）

```bash
# 1. 备份数据库（重要！）
docker exec flux-mysql mysqldump -uroot -p密码 gost > backup_$(date +%Y%m%d_%H%M%S).sql

# 2. 更新代码（如果使用Git）
git pull origin main
# 或手动复制更新的文件到项目目录

# 3. 执行数据库升级（无需停止服务）
docker exec -i flux-mysql mysql -uroot -p密码 gost < update/database_migration_add_multi_hop_tunnel.sql

# 4. 验证数据库升级成功
docker exec flux-mysql mysql -uroot -p密码 gost -e "SHOW COLUMNS FROM tunnel LIKE 'hop_nodes';"
# 应该看到 hop_nodes 字段

# 5. 停止服务
docker-compose -f docker-compose-v4.yml down

# 6. 重新构建并启动
docker-compose -f docker-compose-v4.yml build
docker-compose -f docker-compose-v4.yml up -d

# 7. 验证服务状态
docker-compose -f docker-compose-v4.yml ps
```

---

### 手动部署

```bash
# 1. 备份数据库（重要！）
mysqldump -u用户名 -p数据库名 > backup_$(date +%Y%m%d_%H%M%S).sql

# 2. 执行数据库升级（无需停止服务）
mysql -u用户名 -p数据库名 < update/database_migration_add_multi_hop_tunnel.sql

# 3. 验证数据库升级成功
mysql -u用户名 -p数据库名 -e "SHOW COLUMNS FROM tunnel LIKE 'hop_nodes';"
# 应该看到 hop_nodes 字段

# 4. 停止服务
systemctl stop flux-backend nginx

# 5. 编译后端
cd springboot-backend
mvn clean package -DskipTests

# 6. 构建前端
cd vite-frontend
npm run build

# 7. 部署前端
cp -r dist /var/www/html/flux

# 8. 启动服务
systemctl start flux-backend nginx
```

---

## 📊 数据库升级详解

### 升级内容

数据库升级脚本会执行以下操作：

1. **添加新字段**：在 `tunnel` 表中添加 `hop_nodes` 字段（TEXT类型）
2. **更新注释**：更新 `type` 字段的注释，添加 type=4 的说明
3. **添加索引**：为 `type` 字段添加索引以优化查询性能

### 升级脚本内容

```sql
-- 添加多级节点配置字段
ALTER TABLE `tunnel`
ADD COLUMN `hop_nodes` TEXT DEFAULT NULL
COMMENT '多级节点配置(JSON格式)，存储节点链路信息'
AFTER `ss_config`;

-- 更新type字段注释
ALTER TABLE `tunnel`
MODIFY COLUMN `type` int(10) NOT NULL
COMMENT '1-端口转发, 2-隧道转发, 3-端口复用, 4-多级隧道转发';

-- 添加索引
ALTER TABLE `tunnel`
ADD INDEX `idx_type` (`type`);
```

### 不同环境的升级方式

#### 1. Docker环境（MySQL容器运行中）

```bash
# 方式1: 直接执行SQL文件
docker exec -i flux-mysql mysql -uroot -p密码 gost < update/database_migration_add_multi_hop_tunnel.sql

# 方式2: 进入容器手动执行
docker exec -it flux-mysql mysql -uroot -p密码 gost
# 然后在MySQL命令行中执行：
source /path/to/update/database_migration_add_multi_hop_tunnel.sql;
```

#### 2. 本地MySQL

```bash
# 方式1: 命令行执行
mysql -u用户名 -p数据库名 < update/database_migration_add_multi_hop_tunnel.sql

# 方式2: MySQL客户端执行
mysql -u用户名 -p
USE gost;
SOURCE /path/to/update/database_migration_add_multi_hop_tunnel.sql;
```

#### 3. 远程MySQL

```bash
# 指定主机和端口
mysql -h主机地址 -P端口 -u用户名 -p数据库名 < update/database_migration_add_multi_hop_tunnel.sql
```

#### 4. 使用MySQL Workbench或Navicat等工具

1. 连接到数据库
2. 打开 `update/database_migration_add_multi_hop_tunnel.sql` 文件
3. 执行SQL脚本

### 验证升级成功

执行以下SQL验证：

```sql
-- 1. 检查hop_nodes字段是否存在
SHOW COLUMNS FROM tunnel LIKE 'hop_nodes';

-- 2. 检查type字段注释是否更新
SHOW FULL COLUMNS FROM tunnel WHERE Field='type';

-- 3. 检查索引是否创建
SHOW INDEX FROM tunnel WHERE Key_name='idx_type';

-- 4. 查看表结构
DESC tunnel;
```

### 常见问题

**Q1: 执行升级脚本时提示"字段已存在"**

A: 说明已经升级过了，可以跳过此步骤

**Q2: 提示权限不足**

A: 确保使用的数据库用户有 ALTER TABLE 权限

```sql
-- 授予权限（需要root用户执行）
GRANT ALTER ON gost.* TO '用户名'@'%';
FLUSH PRIVILEGES;
```

**Q3: 升级失败如何回滚**

A: 使用备份恢复：

```bash
# Docker环境
docker exec -i flux-mysql mysql -uroot -p密码 gost < backup_YYYYMMDD_HHMMSS.sql

# 本地环境
mysql -u用户名 -p数据库名 < backup_YYYYMMDD_HHMMSS.sql
```

---

## ✅ 验证升级

访问系统 → 隧道管理 → 新增隧道 → 应该看到"多级隧道转发"选项

---

## 🔄 回滚方案

### Docker部署回滚

```bash
docker-compose -f docker-compose-v4.yml down
docker exec -i flux-mysql mysql -uroot -p密码 gost < backup_YYYYMMDD_HHMMSS.sql
git checkout v1.0.0  # 或恢复备份文件
docker-compose -f docker-compose-v4.yml up -d --build
```

### 手动部署回滚

```bash
systemctl stop flux-backend nginx
mysql -u用户名 -p数据库名 < backup_YYYYMMDD_HHMMSS.sql
# 恢复备份的代码文件
systemctl start flux-backend nginx
```

