package com.konggogi.veganlife.global.config;


import com.konggogi.veganlife.global.MultipartJackson2HttpMessageConverter;
import com.konggogi.veganlife.member.domain.converter.OauthProviderConverter;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final MultipartJackson2HttpMessageConverter multipartJackson2HttpMessageConverter;

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new OauthProviderConverter());
    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(multipartJackson2HttpMessageConverter);
    }
}
