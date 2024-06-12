package com.konggogi.veganlife.global.config;


import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI openAPI(@Value("${springdoc.version}") String version) {
        String AUTH_SCHEME = "JWT Access Token";
        Info info =
                new Info()
                        .title("Vegan Life API 문서")
                        .version(version)
                        .description("우측의 Authorize를 수행한 뒤, API를 테스트 해주세요");
        SecurityRequirement securityRequirement = new SecurityRequirement().addList(AUTH_SCHEME);
        Components components =
                new Components()
                        .addSecuritySchemes(
                                AUTH_SCHEME,
                                new SecurityScheme()
                                        .name(AUTH_SCHEME)
                                        .type(SecurityScheme.Type.HTTP)
                                        .description("bearer는 제외하고 입력해주세요.")
                                        .scheme("bearer")
                                        .bearerFormat("JWT"));

        return new OpenAPI().components(components).addSecurityItem(securityRequirement).info(info);
    }
}
