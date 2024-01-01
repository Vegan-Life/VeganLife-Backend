package com.konggogi.veganlife.comment.domain.mapper;


import com.konggogi.veganlife.comment.controller.dto.request.CommentAddRequest;
import com.konggogi.veganlife.comment.controller.dto.response.CommentAddResponse;
import com.konggogi.veganlife.comment.domain.Comment;
import com.konggogi.veganlife.member.domain.Member;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CommentMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "post", ignore = true)
    @Mapping(target = "parentComment", ignore = true)
    Comment toEntity(Member member, CommentAddRequest commentAddRequest);

    @Mapping(target = "commentId", source = "comment.id")
    CommentAddResponse toCommentAddResponse(Comment comment);
}
