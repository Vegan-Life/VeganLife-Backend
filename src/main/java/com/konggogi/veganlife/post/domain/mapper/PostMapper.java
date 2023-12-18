package com.konggogi.veganlife.post.domain.mapper;


import com.konggogi.veganlife.member.domain.Member;
import com.konggogi.veganlife.post.controller.dto.request.PostAddRequest;
import com.konggogi.veganlife.post.controller.dto.response.PostAddResponse;
import com.konggogi.veganlife.post.domain.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PostMapper {
    Post toEntity(Member member, PostAddRequest postAddRequest);

    @Mapping(source = "post.id", target = "postId")
    PostAddResponse toPostAddResponse(Post post);
}
