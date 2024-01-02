package com.konggogi.veganlife.member.service;


import com.konggogi.veganlife.global.exception.ErrorCode;
import com.konggogi.veganlife.global.security.exception.InvalidOauthTokenException;
import com.konggogi.veganlife.member.domain.oauth.OauthProvider;
import com.konggogi.veganlife.member.domain.oauth.OauthUserInfo;
import com.konggogi.veganlife.member.exception.UnsupportedProviderException;
import com.konggogi.veganlife.member.service.dto.UserInfo;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Service
@RequiredArgsConstructor
public class OauthService {
    private final OauthUserInfoFactory oauthUserInfoFactory;

    @Value("${spring.security.oauth2.client.provider.kakao.user-info-uri}")
    private String KAKAO_USER_INFO_URI;

    @Value("${spring.security.oauth2.client.provider.naver.user-info-uri}")
    private String NAVER_USER_INFO_URI;

    public UserInfo getUserInfo(OauthProvider provider, String token) {
        Map<String, Object> userAttributes = getUserAttributesByToken(provider, token);
        OauthUserInfo oauthUserInfo =
                oauthUserInfoFactory.createOauthUserInfo(provider, userAttributes);
        return oauthUserInfo.getUserInfo();
    }

    private Map<String, Object> getUserAttributesByToken(OauthProvider provider, String token) {
        String userInfoUri = getUserInfoUri(provider);
        try {
            return WebClient.create()
                    .get()
                    .uri(userInfoUri)
                    .headers(httpHeaders -> httpHeaders.setBearerAuth(token))
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                    .block();
        } catch (WebClientResponseException e) {
            throw new InvalidOauthTokenException(ErrorCode.INVALID_OAUTH_TOKEN);
        }
    }

    private String getUserInfoUri(OauthProvider provider) {
        switch (provider) {
            case KAKAO -> {
                return KAKAO_USER_INFO_URI;
            }
            case NAVER -> {
                return NAVER_USER_INFO_URI;
            }
            default -> throw new UnsupportedProviderException(ErrorCode.UNSUPPORTED_PROVIDER);
        }
    }
}
