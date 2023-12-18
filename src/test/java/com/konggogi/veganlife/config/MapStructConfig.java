package com.konggogi.veganlife.config;


import com.konggogi.veganlife.mealdata.domain.mapper.MealDataMapper;
import com.konggogi.veganlife.mealdata.domain.mapper.MealDataMapperImpl;
import com.konggogi.veganlife.member.domain.mapper.AuthMapper;
import com.konggogi.veganlife.member.domain.mapper.AuthMapperImpl;
import com.konggogi.veganlife.member.domain.mapper.MemberMapper;
import com.konggogi.veganlife.member.domain.mapper.MemberMapperImpl;
import com.konggogi.veganlife.post.domain.mapper.PostMapper;
import com.konggogi.veganlife.post.domain.mapper.PostMapperImpl;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class MapStructConfig {

    @Bean
    public MemberMapper memberMapper() {
        return new MemberMapperImpl();
    }

    @Bean
    public MealDataMapper mealDataMapper() {
        return new MealDataMapperImpl();
    }

    @Bean
    public AuthMapper authMapper() {
        return new AuthMapperImpl();
    }

    @Bean
    public PostMapper postMapper() {
        return new PostMapperImpl();
    }
}
