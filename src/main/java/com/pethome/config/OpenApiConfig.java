package com.pethome.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI / Swagger 配置：声明 JWT Bearer 鉴权，方便导入 Apifox 后统一填 Token 调试
 */
@Configuration
public class OpenApiConfig {

    private static final String SCHEME = "bearerAuth";

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("宠物寄养洗护预约管理平台 API")
                        .description("三端共用后端接口；导入 Apifox 后在「鉴权」填 Bearer Token")
                        .version("v1.0.0"))
                .addSecurityItem(new SecurityRequirement().addList(SCHEME))
                .components(new Components()
                        .addSecuritySchemes(SCHEME, new SecurityScheme()
                                .name("Authorization")
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")));
    }
}
