package com.konggogi.veganlife.member.repository;


import com.konggogi.veganlife.member.domain.Member;
import com.konggogi.veganlife.member.domain.oauth.OauthProvider;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByEmail(String email);

    Optional<Member> findById(Long memberId);

    Optional<Member> findByNickname(String nickname);

    Optional<Member> findByOauthProviderAndOauthUserId(
            OauthProvider oauthProvider, String oauthUserId);
}
