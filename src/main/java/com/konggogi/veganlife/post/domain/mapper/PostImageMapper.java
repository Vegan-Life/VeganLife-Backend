package com.konggogi.veganlife.post.domain.mapper;


import com.konggogi.veganlife.post.domain.PostImage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PostImageMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "post", ignore = true)
    PostImage toEntity(String imageUrl);
}
