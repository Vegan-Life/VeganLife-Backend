package com.konggogi.veganlife.global.security.jwt;


import com.konggogi.veganlife.global.util.JwtUtils;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
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

    @Builder
    public RefreshToken(Long id, Long memberId, String token) {
        this.id = id;
        this.memberId = memberId;
        this.token = token;
    }

    public void update(String token) {
        this.token = token;
    }

    public boolean isSameToken(String token) {
        int prefixLength = JwtUtils.BEARER_PREFIX.length();
        return this.token.substring(prefixLength).equals(token);
    }
}
