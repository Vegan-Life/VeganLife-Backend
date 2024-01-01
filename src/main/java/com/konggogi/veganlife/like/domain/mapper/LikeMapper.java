package com.konggogi.veganlife.like.domain.mapper;


import com.konggogi.veganlife.like.domain.CommentLike;
import com.konggogi.veganlife.like.domain.PostLike;
import com.konggogi.veganlife.member.domain.Member;
import com.konggogi.veganlife.post.domain.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface LikeMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "member", source = "member")
    PostLike toPostLike(Member member, Post post);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "member", source = "member")
    CommentLike toCommentLike(Member member, Post post);
}
