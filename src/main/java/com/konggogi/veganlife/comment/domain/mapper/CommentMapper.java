package com.konggogi.veganlife.comment.domain.mapper;


import com.konggogi.veganlife.comment.controller.dto.request.CommentAddRequest;
import com.konggogi.veganlife.comment.controller.dto.response.CommentAddResponse;
import com.konggogi.veganlife.comment.controller.dto.response.CommentDetailsResponse;
import com.konggogi.veganlife.comment.domain.Comment;
import com.konggogi.veganlife.comment.service.dto.CommentDetailsDto;
import com.konggogi.veganlife.comment.service.dto.SubCommentDetailsDto;
import com.konggogi.veganlife.member.domain.Member;
import java.util.List;
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

    @Mapping(target = "author", source = "subComment.member.nickname")
    @Mapping(target = "likeCount", expression = "java(subComment.countLikes())")
    SubCommentDetailsDto toSubCommentDetailsDto(Comment subComment, boolean isLike);

    @Mapping(target = "comment", source = "comment")
    @Mapping(target = "subComments", source = "subComments")
    @Mapping(target = "likeCount", expression = "java(comment.countLikes())")
    CommentDetailsDto toCommentDetailsDto(
            Comment comment, List<SubCommentDetailsDto> subComments, boolean isLike);

    @Mapping(target = "id", source = "commentDetailsDto.comment.id")
    @Mapping(target = "author", source = "commentDetailsDto.comment.member.nickname")
    @Mapping(target = "content", source = "commentDetailsDto.comment.content")
    @Mapping(target = "createdAt", source = "commentDetailsDto.comment.createdAt")
    CommentDetailsResponse toCommentDetailsResponse(CommentDetailsDto commentDetailsDto);
}
