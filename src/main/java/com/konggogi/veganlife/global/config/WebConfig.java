package com.konggogi.veganlife.global.config;


import com.konggogi.veganlife.global.security.interceptor.ReissueInterceptor;
import com.konggogi.veganlife.member.domain.converter.OauthProviderConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {
    private final ReissueInterceptor reissueInterceptor;

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new OauthProviderConverter());
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(reissueInterceptor).addPathPatterns("/api/v1/auth/reissue");
    }
}
