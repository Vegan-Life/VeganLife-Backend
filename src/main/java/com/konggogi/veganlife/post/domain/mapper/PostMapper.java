package com.konggogi.veganlife.post.domain.mapper;


import com.konggogi.veganlife.comment.domain.mapper.CommentMapper;
import com.konggogi.veganlife.comment.service.dto.CommentDetailsDto;
import com.konggogi.veganlife.member.domain.Member;
import com.konggogi.veganlife.post.controller.dto.request.PostFormRequest;
import com.konggogi.veganlife.post.controller.dto.response.PopularTagsResponse;
import com.konggogi.veganlife.post.controller.dto.response.PostAddResponse;
import com.konggogi.veganlife.post.controller.dto.response.PostDetailsResponse;
import com.konggogi.veganlife.post.controller.dto.response.PostSimpleResponse;
import com.konggogi.veganlife.post.domain.Post;
import com.konggogi.veganlife.post.domain.PostImage;
import com.konggogi.veganlife.post.domain.PostTag;
import com.konggogi.veganlife.post.domain.Tag;
import com.konggogi.veganlife.post.service.dto.PostDetailsDto;
import com.konggogi.veganlife.post.service.dto.PostSimpleDto;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring", uses = CommentMapper.class)
public interface PostMapper {
    @Mapping(target = "id", ignore = true)
    Post toEntity(Member member, PostFormRequest postFormRequest);

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
    @Mapping(target = "profileImageUrl", source = "postDetailsDto.post.member.profileImageUrl")
    @Mapping(target = "imageUrls", source = "postDetailsDto.imageUrls")
    @Mapping(target = "commentCount", expression = "java(postDetailsDto.post().countComments())")
    @Mapping(target = "tags", source = "postDetailsDto.tags")
    @Mapping(target = "comments", source = "postDetailsDto.comments")
    @Mapping(target = "title", source = "postDetailsDto.post.title")
    @Mapping(target = "content", source = "postDetailsDto.post.content")
    @Mapping(target = "createdAt", source = "postDetailsDto.post.createdAt")
    PostDetailsResponse toPostDetailsResponse(PostDetailsDto postDetailsDto);

    @Mapping(target = "imageUrls", source = "imageUrls", qualifiedByName = "postImageToString")
    PostSimpleDto toPostSimpleDto(Post post, List<PostImage> imageUrls);

    @Mapping(target = "id", source = "postSimpleDto.post.id")
    @Mapping(target = "title", source = "postSimpleDto.post.title")
    @Mapping(target = "content", source = "postSimpleDto.post.content")
    @Mapping(target = "createdAt", source = "postSimpleDto.post.createdAt")
    @Mapping(
            target = "imageUrl",
            source = "postSimpleDto.imageUrls",
            qualifiedByName = "getThumbnail")
    PostSimpleResponse toPostSimpleResponse(PostSimpleDto postSimpleDto);

    default PopularTagsResponse toPopularTagsResponse(List<Tag> tags) {
        List<String> topTags = tags.stream().map(Tag::getName).toList();
        return new PopularTagsResponse(topTags);
    }

    @Named("postImageToString")
    static String postImageToString(PostImage postImage) {
        return postImage.getImageUrl();
    }

    @Named("postTagsToString")
    static String postTagsToString(PostTag postTag) {
        return postTag.getTag().getName();
    }

    @Named("tagsToString")
    static String tagsToString(Tag tag) {
        return tag.getName();
    }

    @Named("getThumbnail")
    static String getThumbnail(List<String> imageUrls) {
        if (imageUrls.isEmpty()) {
            return null;
        }
        return imageUrls.get(0);
    }
}
