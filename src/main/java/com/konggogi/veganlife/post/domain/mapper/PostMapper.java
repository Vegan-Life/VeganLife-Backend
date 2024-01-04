package com.konggogi.veganlife.post.domain.mapper;


import com.konggogi.veganlife.comment.service.dto.CommentDetailsDto;
import com.konggogi.veganlife.member.domain.Member;
import com.konggogi.veganlife.post.controller.dto.request.PostAddRequest;
import com.konggogi.veganlife.post.controller.dto.response.PostAddResponse;
import com.konggogi.veganlife.post.controller.dto.response.PostDetailsResponse;
import com.konggogi.veganlife.post.domain.Post;
import com.konggogi.veganlife.post.domain.PostImage;
import com.konggogi.veganlife.post.domain.PostTag;
import com.konggogi.veganlife.post.service.dto.PostDetailsDto;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface PostMapper {
    @Mapping(target = "id", ignore = true)
    Post toEntity(Member member, PostAddRequest postAddRequest);

    @Mapping(target = "postId", source = "post.id")
    PostAddResponse toPostAddResponse(Post post);

    @Mapping(target = "post", source = "post")
    @Mapping(target = "comments", source = "comments")
    @Mapping(target = "likeCount", expression = "java(post.countLikes())")
    @Mapping(target = "imageUrls", source = "post.imageUrls", qualifiedByName = "postImageToString")
    @Mapping(target = "tags", source = "post.tags", qualifiedByName = "postTagsToString")
    PostDetailsDto toPostDetailsDto(Post post, List<CommentDetailsDto> comments, boolean isLike);

    @Mapping(target = "author", source = "postDetailsDto.post.member.nickname")
    @Mapping(target = "vegetarianType", source = "postDetailsDto.post.member.vegetarianType")
    @Mapping(target = "imageUrls", source = "postDetailsDto.imageUrls")
    @Mapping(target = "tags", source = "postDetailsDto.tags")
    @Mapping(target = "comments", source = "postDetailsDto.comments")
    @Mapping(target = "title", source = "postDetailsDto.post.title")
    @Mapping(target = "content", source = "postDetailsDto.post.content")
    @Mapping(target = "createdAt", source = "postDetailsDto.post.createdAt")
    PostDetailsResponse toPostDetailsResponse(PostDetailsDto postDetailsDto);

    @Named("postImageToString")
    static String postImageToString(PostImage postImage) {
        return postImage.getImageUrl();
    }

    @Named("postTagsToString")
    static String postTagsToString(PostTag postTag) {
        return postTag.getTag().getName();
    }
}
