package com.konggogi.veganlife.config;


import com.konggogi.veganlife.mealdata.domain.mapper.MealDataMapper;
import com.konggogi.veganlife.mealdata.domain.mapper.MealDataMapperImpl;
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
    public MealDataMapper mealDataMapper() {
        return new MealDataMapperImpl();
    }
}
