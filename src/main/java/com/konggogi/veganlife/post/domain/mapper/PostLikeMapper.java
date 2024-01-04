package com.konggogi.veganlife.post.domain.mapper;


import com.konggogi.veganlife.member.domain.Member;
import com.konggogi.veganlife.post.domain.PostLike;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PostLikeMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "post", ignore = true)
    PostLike toPostLike(Member member);
}
