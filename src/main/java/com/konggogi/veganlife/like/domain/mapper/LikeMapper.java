package com.konggogi.veganlife.like.domain.mapper;


import com.konggogi.veganlife.like.domain.PostLike;
import com.konggogi.veganlife.member.domain.Member;
import com.konggogi.veganlife.post.domain.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface LikeMapper {
    @Mapping(target = "id", ignore = true)
    PostLike toEntity(Member member, Post post);
}
