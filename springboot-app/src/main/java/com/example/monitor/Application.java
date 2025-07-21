package com.example.monitor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import io.micrometer.core.annotation.Timed;
import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
@RestController
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    /**
     * 示例API接口，用于演示应用监控
     */
    @Timed(value = "api.requests", description = "API请求响应时间")
    @GetMapping("/api/demo")
    public Map<String, Object> demoApi() {
        Map<String, Object> result = new HashMap<>();
        result.put("status", "success");
        result.put("message", "Spring Boot监控示例接口");
        result.put("data", System.currentTimeMillis());
        return result;
    }

    /**
     * 健康检查接口扩展
     */
    @GetMapping("/health/custom")
    public Map<String, Object> customHealthCheck() {
        Map<String, Object> healthStatus = new HashMap<>();
        healthStatus.put("status", "UP");
        healthStatus.put("service", "springboot-monitor-demo");
        healthStatus.put("version", "1.0.0");
        return healthStatus;
    }
}