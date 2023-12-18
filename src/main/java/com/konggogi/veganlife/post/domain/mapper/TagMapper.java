package com.konggogi.veganlife.post.domain.mapper;


import com.konggogi.veganlife.post.domain.PostTag;
import com.konggogi.veganlife.post.domain.Tag;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TagMapper {
    @Mapping(target = "id", ignore = true)
    Tag toEntity(String name);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "post", ignore = true)
    PostTag toPostTag(Tag tag);
}
