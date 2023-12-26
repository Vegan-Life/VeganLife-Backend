package com.konggogi.veganlife.config;


import com.konggogi.veganlife.mealdata.domain.mapper.MealDataMapper;
import com.konggogi.veganlife.mealdata.domain.mapper.MealDataMapperImpl;
import com.konggogi.veganlife.meallog.domain.mapper.MealLogMapper;
import com.konggogi.veganlife.meallog.domain.mapper.MealLogMapperImpl;
import com.konggogi.veganlife.meallog.domain.mapper.MealMapper;
import com.konggogi.veganlife.meallog.domain.mapper.MealMapperImpl;
import com.konggogi.veganlife.member.domain.mapper.*;
import com.konggogi.veganlife.post.domain.mapper.*;
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
    public MealMapper mealMapper() {
        return new MealMapperImpl();
    }

    @Bean
    public MealLogMapper mealLogMapper() {
        return new MealLogMapperImpl();
    }

    @Bean
    public PostMapper postMapper() {
        return new PostMapperImpl();
    }

    @Bean
    public TagMapper tagMapper() {
        return new TagMapperImpl();
    }

    @Bean
    public PostImageMapper postImageMapper() {
        return new PostImageMapperImpl();
    }

    @Bean
    public PostLikeMapper postLikeMapper() {
        return new PostLikeMapperImpl();
    }

    @Bean
    public NutrientsMapper nutrientsMapper() {
        return new NutrientsMapperImpl();
    }
}
