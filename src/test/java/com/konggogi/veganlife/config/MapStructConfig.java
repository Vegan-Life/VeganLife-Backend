package com.konggogi.veganlife.config;


import com.konggogi.veganlife.mealdata.domain.mapper.MealDataDtoMapper;
import com.konggogi.veganlife.mealdata.domain.mapper.MealDataDtoMapperImpl;
import com.konggogi.veganlife.member.domain.mapper.AuthMapper;
import com.konggogi.veganlife.member.domain.mapper.AuthMapperImpl;
import com.konggogi.veganlife.member.domain.mapper.MemberMapper;
import com.konggogi.veganlife.member.domain.mapper.MemberMapperImpl;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class MapStructConfig {

    @Bean
    public MemberMapper memberMapper() {
        return new MemberMapperImpl();
    }

    @Bean
    public MealDataDtoMapper mealDataDtoMapper() {
        return new MealDataDtoMapperImpl();
    }

    @Bean
    public AuthMapper authMapper() {
        return new AuthMapperImpl();
    }
}
