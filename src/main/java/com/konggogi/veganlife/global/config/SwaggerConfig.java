package com.konggogi.veganlife.global.config;


import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(
        servers = {
            @Server(url = "https://dev.konggogi.store", description = "개발 서버"),
            @Server(url = "http://localhost:8080", description = "로컬 서버")
        })
@Configuration
public class SwaggerConfig {
    private final String AUTH_SCHEME_NAME = "JWT Access Token";
    private final String AUTH_SCHEME = "bearer";
    private final String BEARER_FORMAT = "JWT";

    @Bean
    public OpenAPI openAPI(@Value("${springdoc.version}") String version) {
        Info info =
                new Info()
                        .title("Vegan Life API Documentation")
                        .version(version)
                        .description("우측의 Authorize를 수행한 뒤, API를 테스트 해주세요");

        SecurityRequirement securityRequirement =
                new SecurityRequirement().addList(AUTH_SCHEME_NAME);
        Components components =
                new Components()
                        .addSecuritySchemes(
                                AUTH_SCHEME_NAME,
                                new SecurityScheme()
                                        .name(AUTH_SCHEME_NAME)
                                        .type(SecurityScheme.Type.HTTP)
                                        .description("bearer는 제외하고 입력해주세요.")
                                        .scheme(AUTH_SCHEME)
                                        .bearerFormat(BEARER_FORMAT));

        return new OpenAPI().components(components).addSecurityItem(securityRequirement).info(info);
    }
}
