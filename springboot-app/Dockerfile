# 构建阶段使用国内Maven镜像加速依赖下载
FROM maven:3.8.5-openjdk-8 AS build
WORKDIR /app

# 复制pom.xml并缓存依赖
COPY pom.xml .
# 使用阿里云Maven镜像
RUN mvn clean package -Dmaven.test.skip=true -Dmaven.repo.local=/root/.m2/repository -s https://maven.aliyun.com/nexus/content/groups/public/settings.xml

# 复制源代码
COPY src ./src

# 构建应用
RUN mvn package -Dmaven.test.skip=true -Dmaven.repo.local=/root/.m2/repository -s https://maven.aliyun.com/nexus/content/groups/public/settings.xml

# 运行阶段使用轻量级JRE
FROM openjdk:8-jre-alpine
WORKDIR /app

# 设置时区
RUN apk --no-cache add tzdata && ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime

# 创建应用目录
RUN mkdir -p /app/logs /app/config

# 复制构建产物
COPY --from=build /app/target/*.jar app.jar

# 复制配置文件
COPY ./config /app/config

# 健康检查
HEALTHCHECK --interval=30s --timeout=3s --retries=3 \
  CMD wget -q --spider http://localhost:8080/actuator/health || exit 1

# 启动命令
ENTRYPOINT ["java", "-jar", "app.jar", "--spring.profiles.active=prod", "--spring.config.location=file:/app/config/"]

# 安全配置
USER 1000

# 暴露端口
EXPOSE 8080