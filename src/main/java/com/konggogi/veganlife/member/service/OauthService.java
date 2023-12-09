package com.konggogi.veganlife.member.service;


import com.konggogi.veganlife.member.domain.Member;
import com.konggogi.veganlife.member.domain.Role;
import com.konggogi.veganlife.member.domain.oauth.OauthUserInfo;
import com.konggogi.veganlife.member.repository.MemberRepository;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OauthService {
    private final MemberRepository memberRepository;
    private final OauthUserInfoFactory oauthUserInfoFactory;

    @Value("${spring.security.oauth2.client.provider.kakao.user-info-uri}")
    private String KAKAO_USER_INFO_URI;

    public Member createMemberFromToken(String provider, String token) {
        Map<String, Object> userAttributes = getUserAttributesByToken(provider, token);
        OauthUserInfo oauthUserInfo =
                oauthUserInfoFactory.createOauthUserInfo(provider, userAttributes);
        String email = oauthUserInfo.getEmail();
        String birthYear = oauthUserInfo.getBirthYear();
        String phoneNumber = oauthUserInfo.getPhoneNumber();
        // TODO gender 추가
        return Member.builder()
                .email(email)
                .birthYear(birthYear)
                .phoneNumber(phoneNumber)
                .role(Role.USER)
                .build();
    }

    private Map<String, Object> getUserAttributesByToken(String provider, String token) {
        // TODO naver 요청 URI 추가
        String userInfoUri = provider.equals("kakao") ? KAKAO_USER_INFO_URI : "";
        return WebClient.create()
                .get()
                .uri(userInfoUri)
                .headers(httpHeaders -> httpHeaders.setBearerAuth(token))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                .block();
    }

    public boolean isSignedMember(Member member) {
        return memberRepository.findByEmail(member.getEmail()).isPresent();
    }
}
