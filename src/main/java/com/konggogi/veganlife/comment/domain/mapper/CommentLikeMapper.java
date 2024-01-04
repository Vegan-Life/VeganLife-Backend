package com.konggogi.veganlife.comment.domain.mapper;


import com.konggogi.veganlife.comment.domain.CommentLike;
import com.konggogi.veganlife.member.domain.Member;
import com.konggogi.veganlife.post.domain.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CommentLikeMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "comment", ignore = true)
    @Mapping(target = "member", source = "member")
    CommentLike toCommentLike(Member member, Post post);
}
