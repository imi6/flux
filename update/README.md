# Flux 多级隧道转发功能 - 升级指南

## � 快速升级

### Docker部署（推荐）

```bash
# 1. 备份数据库
docker exec flux-mysql mysqldump -uroot -p密码 gost > backup_$(date +%Y%m%d_%H%M%S).sql

# 2. 停止服务
docker-compose -f docker-compose-v4.yml down

# 3. 更新代码（如果使用Git）
git pull origin main

# 4. 执行数据库升级
docker exec -i flux-mysql mysql -uroot -p密码 gost < update/database_migration_add_multi_hop_tunnel.sql

# 5. 重新构建并启动
docker-compose -f docker-compose-v4.yml build
docker-compose -f docker-compose-v4.yml up -d

# 6. 验证升级
docker-compose -f docker-compose-v4.yml ps
docker exec flux-mysql mysql -uroot -p密码 gost -e "SHOW COLUMNS FROM tunnel LIKE 'hop_nodes';"
```

---

### 手动部署

```bash
# 1. 备份数据库
mysqldump -u用户名 -p数据库名 > backup_$(date +%Y%m%d_%H%M%S).sql

# 2. 停止服务
systemctl stop flux-backend nginx

# 3. 执行数据库升级
mysql -u用户名 -p数据库名 < update/database_migration_add_multi_hop_tunnel.sql

# 4. 编译后端
cd springboot-backend
mvn clean package -DskipTests

# 5. 构建前端
cd vite-frontend
npm run build

# 6. 部署前端
cp -r dist /var/www/html/flux

# 7. 启动服务
systemctl start flux-backend nginx
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

