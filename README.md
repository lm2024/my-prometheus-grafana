# Prometheus + Grafana 生产级监控平台搭建指南

## 项目概述
本项目提供了一个生产级别的Prometheus和Grafana监控平台搭建方案，包含完整的持久化配置和最佳实践参数。

## 环境要求
- Docker: 20.10.x 或更高版本
- Docker Compose: 2.x 或更高版本
- 操作系统: Linux/macOS
- 推荐配置: 至少2核CPU, 4GB内存, 20GB磁盘空间

## 目录结构
```
./
├── docker-compose.yml       # 容器编排配置
├── prometheus/
│   ├── conf/
│   │   ├── prometheus.yml   # Prometheus主配置
│   │   └── alert.rules.yml  # 告警规则配置
│   └── data/                # Prometheus数据持久化目录
├── grafana/
│   ├── conf/
│   │   └── grafana.ini      # Grafana配置
│   ├── data/                # Grafana数据持久化目录
│   └── logs/                # Grafana日志目录
└── README.md                # 部署文档
```

## 配置参数说明
### 可变参数（根据服务器配置调整）
1. **内存配置**（在docker-compose.yml中）
   - Prometheus: 默认限制2G内存，建议生产环境至少4G
   - Grafana: 默认限制1G内存，建议生产环境至少2G

2. **存储保留时间**（在prometheus.yml中）
   - `--storage.tsdb.retention.time=30d`: 默认保留30天数据
   - 根据数据量和磁盘空间调整，生产环境建议至少保留15天

3. **抓取间隔**（在prometheus.yml中）
   - `scrape_interval: 15s`: 默认15秒抓取一次
   - 监控目标多可适当延长至30s或60s

### 安全参数
1. Grafana默认管理员账号
   - 用户名: admin
   - 密码: admin@123 (首次登录请修改)

2. 所有敏感配置已通过环境变量注入，避免硬编码

## 部署步骤
### 1. 准备环境
```bash
# 检查Docker和Docker Compose是否安装
docker --version
docker compose version
```

### 2. 拉取镜像（可选）
如果直接启动失败，可手动拉取镜像：
```bash
# 官方镜像
 docker pull prom/prometheus:v2.37.0
docker pull grafana/grafana:8.5.22

# 或使用国内镜像
# docker pull registry.cn-hangzhou.aliyuncs.com/monitoring/prometheus:v2.37.0
# docker pull registry.cn-hangzhou.aliyuncs.com/monitoring/grafana:8.5.22
```

### 3. 启动服务
```bash
# 在项目根目录执行
docker compose up -d
```

### 4. 验证服务状态
```bash
# 检查容器状态
docker compose ps

# 查看日志
docker compose logs -f
```

## 访问服务
- Prometheus: http://localhost:9090
- Grafana: http://localhost:3000

## Grafana初始配置
1. 登录后立即修改管理员密码
2. 添加Prometheus数据源：
   - 地址: http://prometheus:9090
   - 其他保持默认
3. 导入常用仪表盘：
   - Prometheus监控: 模板ID 1860
   - 系统监控: 模板ID 893

## 扩展监控目标
### MySQL监控
已添加MySQL 5.7和mysqld-exporter监控组件，配置包含：
- 完整的MySQL服务部署（含持久化存储）
- mysqld-exporter指标采集
- Prometheus抓取配置
- 生产级资源限制和健康检查

### Spring Boot应用监控
已集成Spring Boot 2.7.x应用监控示例，包含：
- 完整的Spring Boot应用工程（Java 8兼容）
- Actuator + Prometheus指标暴露
- 自定义业务指标示例
- 与MySQL数据库集成
- Docker化部署配置

## 后续配置步骤
1. **安全加固**

## 维护与备份
### 数据备份
```bash
# 备份Prometheus数据
tar -czf prometheus_backup_$(date +%Y%m%d).tar.gz ./prometheus/data

# 备份Grafana数据
tar -czf grafana_backup_$(date +%Y%m%d).tar.gz ./grafana/data
```

### 服务重启
```bash
docker compose restart
```

### 版本升级
1. 修改docker-compose.yml中的镜像版本
2. 执行：
```bash
docker compose up -d
```

## 注意事项
1. **镜像选择**：
   - 国内用户建议优先使用阿里云或网易云镜像
   - 如遇镜像拉取失败，尝试切换镜像源

2. **持久化**：
   - 所有数据均保存在当前目录下，删除容器不会丢失数据
   - 定期备份data目录确保数据安全

3. **生产环境建议**：
   - 使用外部存储（如NFS）挂载数据目录
   - 配置监控告警通知（邮件、钉钉等）
   - 定期更新镜像版本和安全补丁

4. **性能优化**：
   - 监控目标超过100个时，考虑使用Prometheus联邦集群
   - 大量时序数据可配置远程存储（如Thanos、Cortex）