package com.konggogi.veganlife.post.domain;


import com.konggogi.veganlife.global.domain.TimeStamped;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Tag extends TimeStamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tag_id")
    private Long id;

    @Column(nullable = false)
    private String name;
}
