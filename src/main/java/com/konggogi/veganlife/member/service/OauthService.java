package com.konggogi.veganlife.member.service;


import com.konggogi.veganlife.global.security.jwt.JwtProvider;
import com.konggogi.veganlife.member.domain.Gender;
import com.konggogi.veganlife.member.domain.Member;
import com.konggogi.veganlife.member.domain.Role;
import com.konggogi.veganlife.member.domain.oauth.OauthUserInfo;
import com.konggogi.veganlife.member.dto.response.OauthLoginResponse;
import com.konggogi.veganlife.member.repository.MemberRepository;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OauthService {
    private final JwtProvider jwtProvider;
    private final MemberRepository memberRepository;
    private final OauthUserInfoFactory oauthUserInfoFactory;

    public OauthLoginResponse loginWithToken(String provider, String token) {
        Member member = createMemberFromToken(provider, token);
        String accessToken = jwtProvider.createToken(member.getEmail());
        String refreshToken = jwtProvider.createRefreshToken(member.getEmail());
        return createOauthLoginResponse(member, accessToken, refreshToken);
    }

    private OauthLoginResponse createOauthLoginResponse(
            Member member, String accessToken, String refreshToken) {
        return memberRepository
                .findByEmail(member.getEmail())
                .map(foundMember -> new OauthLoginResponse(true, accessToken, refreshToken))
                .orElse(new OauthLoginResponse(false, accessToken, refreshToken));
    }

    private Member createMemberFromToken(String provider, String token) {
        Map<String, Object> userAttributes = getUserAttributesByToken(token);
        OauthUserInfo oauthUserInfo =
                oauthUserInfoFactory.createOauthUserInfo(provider, userAttributes);
        String email = oauthUserInfo.getEmail();
        String imageUrl = oauthUserInfo.getProfileImageUrl();
        Gender gender = oauthUserInfo.getGender();
        String phoneNumber = oauthUserInfo.getPhoneNumber();
        return Member.builder()
                .email(email)
                .profileImageUrl(imageUrl)
                .gender(gender)
                .phoneNumber(phoneNumber)
                .role(Role.USER)
                .build();
    }

    private Map<String, Object> getUserAttributesByToken(String token) {
        return WebClient.create()
                .get()
                .uri("https://kapi.kakao.com/v2/user/me")
                .headers(httpHeaders -> httpHeaders.setBearerAuth(token))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                .block();
    }
}
