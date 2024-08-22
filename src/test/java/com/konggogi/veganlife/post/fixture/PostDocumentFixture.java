package com.konggogi.veganlife.post.fixture;


import com.konggogi.veganlife.post.domain.document.PostDocument;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.List;
import org.springframework.util.ReflectionUtils;

public enum PostDocumentFixture {
    DEFAULT("제목", "내용", List.of("#태그"));
    private final String title;
    private final String content;
    private final List<String> tags;

    PostDocumentFixture(String title, String content, List<String> tags) {
        this.title = title;
        this.content = content;
        this.tags = tags;
    }

    public PostDocument get() {
        return PostDocument.builder().title(title).content(content).tags(tags).build();
    }

    public PostDocument getWithId(Long id) {
        PostDocument postDoc =
                PostDocument.builder().id(id).title(title).content(content).tags(tags).build();
        return setCreatedAt(postDoc, LocalDate.now());
    }

    public PostDocument getWithTitle(Long id, String title) {
        PostDocument postDoc =
                PostDocument.builder().id(id).title(title).content(content).tags(tags).build();
        return setCreatedAt(postDoc, LocalDate.now());
    }

    private PostDocument setCreatedAt(PostDocument postDoc, LocalDate date) {
        Field createdAtField = ReflectionUtils.findField(PostDocument.class, "createdAt");
        ReflectionUtils.makeAccessible(createdAtField);
        ReflectionUtils.setField(createdAtField, postDoc, date.atStartOfDay());
        return postDoc;
    }
}
