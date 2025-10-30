#!/bin/bash

# Flux 系统升级脚本
# 用途：自动升级数据库和应用容器，添加端口复用功能

set -e  # 遇到错误立即退出

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# 日志函数
log_info() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

log_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

log_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

log_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# 检查是否为 root 用户或有 sudo 权限
check_permissions() {
    if [ "$EUID" -ne 0 ] && ! sudo -n true 2>/dev/null; then
        log_error "请使用 root 用户或具有 sudo 权限的用户运行此脚本"
        exit 1
    fi
}

# 检查 Docker 是否安装
check_docker() {
    if ! command -v docker &> /dev/null; then
        log_error "Docker 未安装，请先安装 Docker"
        exit 1
    fi
    log_success "Docker 已安装"
}

# 检查 docker-compose 是否安装
check_docker_compose() {
    if ! command -v docker-compose &> /dev/null; then
        log_error "docker-compose 未安装，请先安装 docker-compose"
        exit 1
    fi
    log_success "docker-compose 已安装"
}

# 加载环境变量
load_env() {
    if [ ! -f .env ]; then
        log_error ".env 文件不存在"
        exit 1
    fi
    
    # 导出环境变量
    export $(cat .env | grep -v '^#' | xargs)
    log_success "环境变量已加载"
}

# 检查容器是否运行
check_containers() {
    log_info "检查容器状态..."
    
    if ! docker ps | grep -q gost-mysql; then
        log_error "MySQL 容器未运行，请先启动容器"
        exit 1
    fi
    
    log_success "MySQL 容器正在运行"
}

# 备份数据库
backup_database() {
    log_info "开始备份数据库..."
    
    BACKUP_FILE="backup_$(date +%Y%m%d_%H%M%S).sql"
    
    docker exec gost-mysql mysqldump -u"${DB_USER}" -p"${DB_PASSWORD}" "${DB_NAME}" > "${BACKUP_FILE}" 2>/dev/null
    
    if [ $? -eq 0 ]; then
        log_success "数据库备份成功: ${BACKUP_FILE}"
        BACKUP_SIZE=$(du -h "${BACKUP_FILE}" | cut -f1)
        log_info "备份文件大小: ${BACKUP_SIZE}"
    else
        log_error "数据库备份失败"
        exit 1
    fi
}

# 执行数据库迁移
migrate_database() {
    log_info "开始执行数据库迁移..."
    
    if [ ! -f database_migration_add_port_reuse.sql ]; then
        log_error "迁移脚本文件不存在: database_migration_add_port_reuse.sql"
        exit 1
    fi
    
    docker exec -i gost-mysql mysql -u"${DB_USER}" -p"${DB_PASSWORD}" "${DB_NAME}" < database_migration_add_port_reuse.sql 2>/dev/null
    
    if [ $? -eq 0 ]; then
        log_success "数据库迁移成功"
    else
        log_error "数据库迁移失败"
        log_warning "正在尝试恢复数据库..."
        docker exec -i gost-mysql mysql -u"${DB_USER}" -p"${DB_PASSWORD}" "${DB_NAME}" < "${BACKUP_FILE}" 2>/dev/null
        exit 1
    fi
}

# 验证数据库迁移
verify_migration() {
    log_info "验证数据库迁移结果..."
    
    # 检查 ss_config 字段是否存在
    RESULT=$(docker exec gost-mysql mysql -u"${DB_USER}" -p"${DB_PASSWORD}" "${DB_NAME}" -e "SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE table_schema = '${DB_NAME}' AND table_name = 'tunnel' AND column_name = 'ss_config';" -s -N 2>/dev/null)
    
    if [ "$RESULT" = "1" ]; then
        log_success "ss_config 字段已成功添加"
    else
        log_error "ss_config 字段添加失败"
        exit 1
    fi
}

# 更新容器
update_containers() {
    log_info "开始更新容器..."
    
    # 停止并删除旧容器
    log_info "停止旧容器..."
    docker stop springboot-backend vite-frontend 2>/dev/null || true
    
    log_info "删除旧容器..."
    docker rm springboot-backend vite-frontend 2>/dev/null || true
    
    # 删除旧镜像
    log_info "删除旧镜像..."
    docker rmi xydgg/springboot-backend:latest 2>/dev/null || true
    docker rmi xydgg/vite-frontend:latest 2>/dev/null || true
    
    # 拉取新镜像
    log_info "拉取新镜像..."
    docker-compose pull
    
    # 启动新容器
    log_info "启动新容器..."
    docker-compose up -d
    
    log_success "容器更新完成"
}

# 等待容器启动
wait_for_containers() {
    log_info "等待容器启动..."
    
    # 等待后端容器健康检查通过
    RETRY_COUNT=0
    MAX_RETRIES=30
    
    while [ $RETRY_COUNT -lt $MAX_RETRIES ]; do
        HEALTH_STATUS=$(docker inspect --format='{{.State.Health.Status}}' springboot-backend 2>/dev/null || echo "starting")
        
        if [ "$HEALTH_STATUS" = "healthy" ]; then
            log_success "后端容器已就绪"
            break
        fi
        
        log_info "等待后端容器启动... ($((RETRY_COUNT+1))/$MAX_RETRIES)"
        sleep 3
        RETRY_COUNT=$((RETRY_COUNT+1))
    done
    
    if [ $RETRY_COUNT -eq $MAX_RETRIES ]; then
        log_warning "后端容器启动超时，请手动检查"
    fi
    
    # 检查前端容器
    if docker ps | grep -q vite-frontend; then
        log_success "前端容器已启动"
    else
        log_warning "前端容器未启动，请手动检查"
    fi
}

# 显示容器状态
show_status() {
    log_info "当前容器状态："
    echo ""
    docker ps -a --filter "name=gost-mysql" --filter "name=springboot-backend" --filter "name=vite-frontend" --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}"
    echo ""
}

# 显示升级总结
show_summary() {
    echo ""
    echo "=========================================="
    log_success "升级完成！"
    echo "=========================================="
    echo ""
    log_info "升级内容："
    echo "  ✅ 数据库已备份: ${BACKUP_FILE}"
    echo "  ✅ 数据库已迁移（添加 ss_config 字段）"
    echo "  ✅ 前端容器已更新"
    echo "  ✅ 后端容器已更新"
    echo ""
    log_info "新功能："
    echo "  🎉 端口复用功能已启用"
    echo "  🎉 支持 SS 节点配置"
    echo "  🎉 端口复用诊断功能"
    echo ""
    log_info "访问地址："
    echo "  前端: http://your-server-ip:${FRONTEND_PORT}"
    echo "  后端: http://your-server-ip:${BACKEND_PORT}"
    echo ""
    log_info "查看日志："
    echo "  docker logs -f springboot-backend"
    echo "  docker logs -f vite-frontend"
    echo ""
}

# 主函数
main() {
    echo ""
    echo "=========================================="
    echo "  Flux 系统升级脚本"
    echo "  版本: 1.0.0"
    echo "  日期: $(date +%Y-%m-%d)"
    echo "=========================================="
    echo ""
    
    # 确认升级
    read -p "$(echo -e ${YELLOW}是否继续升级？[y/N]: ${NC})" -n 1 -r
    echo
    if [[ ! $REPLY =~ ^[Yy]$ ]]; then
        log_info "升级已取消"
        exit 0
    fi
    
    # 执行升级步骤
    check_permissions
    check_docker
    check_docker_compose
    load_env
    check_containers
    backup_database
    migrate_database
    verify_migration
    update_containers
    wait_for_containers
    show_status
    show_summary
}

# 运行主函数
main

