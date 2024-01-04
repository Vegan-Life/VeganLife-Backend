package com.konggogi.veganlife.post.domain.mapper;


import com.konggogi.veganlife.comment.domain.mapper.CommentMapper;
import com.konggogi.veganlife.comment.service.dto.CommentDetailsDto;
import com.konggogi.veganlife.member.domain.Member;
import com.konggogi.veganlife.post.controller.dto.request.PostAddRequest;
import com.konggogi.veganlife.post.controller.dto.response.PostAddResponse;
import com.konggogi.veganlife.post.controller.dto.response.PostDetailsResponse;
import com.konggogi.veganlife.post.domain.Post;
import com.konggogi.veganlife.post.domain.PostImage;
import com.konggogi.veganlife.post.domain.PostTag;
import com.konggogi.veganlife.post.service.dto.PostDetailsDto;
import java.util.Collections;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = CommentMapper.class)
public interface PostMapper {
    @Mapping(target = "id", ignore = true)
    Post toEntity(Member member, PostAddRequest postAddRequest);

    @Mapping(target = "postId", source = "post.id")
    PostAddResponse toPostAddResponse(Post post);

    @Mapping(target = "post", source = "post")
    @Mapping(target = "comments", source = "comments")
    @Mapping(target = "likeCount", expression = "java(post.countLikes())")
    PostDetailsDto toPostDetailsDto(Post post, List<CommentDetailsDto> comments, boolean isLike);

    @Mapping(target = "author", source = "postDetailsDto.post.member.nickname")
    @Mapping(target = "vegetarianType", source = "postDetailsDto.post.member.vegetarianType")
    @Mapping(target = ".", source = "postDetailsDto.post")
    PostDetailsResponse toPostDetailsResponse(PostDetailsDto postDetailsDto);

    default List<String> mapImageUrls(List<PostImage> postImages) {
        if (postImages == null) {
            return Collections.emptyList();
        }
        return postImages.stream().map(PostImage::getImageUrl).toList();
    }

    default List<String> mapTags(List<PostTag> postTags) {
        if (postTags == null) {
            return Collections.emptyList();
        }
        return postTags.stream().map(postTag -> postTag.getTag().getName()).toList();
    }
}
