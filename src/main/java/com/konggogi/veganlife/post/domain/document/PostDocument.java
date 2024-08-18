package com.konggogi.veganlife.post.domain.document;


import jakarta.persistence.Id;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Document(indexName = "posts", writeTypeHint = WriteTypeHint.FALSE)
@Setting(settingPath = "elastic/elastic-setting.json")
@Mapping(mappingPath = "elastic/post-mapping.json")
public class PostDocument {
    @Id private Long id;
    private String title;
    private String content;

    @Field(
            type = FieldType.Date,
            format = {},
            pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS||epoch_millis")
    private LocalDateTime createdAt;

    private String imageUrl;
    private List<String> tags;

    @Builder
    public PostDocument(
            Long id,
            String title,
            String content,
            LocalDateTime createdAt,
            String imageUrl,
            List<String> tags) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.createdAt = createdAt;
        this.imageUrl = imageUrl;
        this.tags = tags;
    }
}
