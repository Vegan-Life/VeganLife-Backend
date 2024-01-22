package com.konggogi.veganlife.recipe.domain.mapper;


import com.konggogi.veganlife.member.domain.Member;
import com.konggogi.veganlife.recipe.domain.Recipe;
import com.konggogi.veganlife.recipe.domain.RecipeLike;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RecipeLikeMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(source = "member", target = "member")
    RecipeLike toEntity(Recipe recipe, Member member);
}
