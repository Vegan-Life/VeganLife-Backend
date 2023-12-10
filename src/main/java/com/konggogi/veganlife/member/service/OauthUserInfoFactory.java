package com.konggogi.veganlife.member.service;


import com.konggogi.veganlife.global.exception.ErrorCode;
import com.konggogi.veganlife.member.domain.oauth.KakaoUserInfo;
import com.konggogi.veganlife.member.domain.oauth.NaverUserInfo;
import com.konggogi.veganlife.member.domain.oauth.OauthProvider;
import com.konggogi.veganlife.member.domain.oauth.OauthUserInfo;
import com.konggogi.veganlife.member.exception.UnsupportedProviderException;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class OauthUserInfoFactory {
    public OauthUserInfo createOauthUserInfo(
            OauthProvider provider, Map<String, Object> userAttributes) {
        if (OauthProvider.KAKAO == provider) {
            return new KakaoUserInfo(userAttributes);
        } else if (OauthProvider.NAVER == provider) {
            return new NaverUserInfo(userAttributes);
        } else {
            throw new UnsupportedProviderException(ErrorCode.UNSUPPORTED_PROVIDER);
        }
    }
}
