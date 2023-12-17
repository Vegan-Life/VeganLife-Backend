package com.konggogi.veganlife.post.domain;


import com.konggogi.veganlife.global.domain.TimeStamped;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Post extends TimeStamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Long id;

    @Column(length = 1000)
    private String content;

    @OneToMany(mappedBy = "post", orphanRemoval = true)
    private List<PostImage> imageUrls = new ArrayList<>();

    @OneToMany(mappedBy = "post", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<PostTag> tags = new ArrayList<>();
}
