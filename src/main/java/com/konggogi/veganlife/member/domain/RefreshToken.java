package com.konggogi.veganlife.member.domain;


import com.konggogi.veganlife.global.security.jwt.JwtProperties;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "refresh_token_id")
    private Long id;

    private String token;
    private Long memberId;

    public RefreshToken(String token, Long id) {
        this.token = token;
        this.memberId = id;
    }

    public void updateToken(String token) {
        this.token = token;
    }

    public boolean isSameToken(String token) {
        int prefixLength = JwtProperties.BEARER_PREFIX.length();
        return this.token.substring(prefixLength).equals(token);
    }
}
