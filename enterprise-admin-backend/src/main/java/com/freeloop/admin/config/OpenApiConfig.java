package com.freeloop.admin.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "企业权限管理系统 API",
                version = "v1",
                description = "企业权限管理系统后端接口文档，当前包含用户管理模块"
        )
)
public class OpenApiConfig {
}