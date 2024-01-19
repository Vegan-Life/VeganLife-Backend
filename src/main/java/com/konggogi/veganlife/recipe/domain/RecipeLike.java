package com.konggogi.veganlife.recipe.domain;


import com.konggogi.veganlife.member.domain.Member;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"recipe_id", "member_id"})})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RecipeLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "recipe_like_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "recipe_id")
    private Recipe recipe;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    public RecipeLike(Long id, Recipe recipe, Member member) {
        this.id = id;
        this.recipe = recipe;
        this.member = member;
    }
}
