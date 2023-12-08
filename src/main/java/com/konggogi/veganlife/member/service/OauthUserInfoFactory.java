package com.konggogi.veganlife.member.service;


import com.konggogi.veganlife.global.exception.ErrorCode;
import com.konggogi.veganlife.member.domain.oauth.KakaoUserInfo;
import com.konggogi.veganlife.member.domain.oauth.OauthUserInfo;
import com.konggogi.veganlife.member.exception.UnsupportedProviderException;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class OauthUserInfoFactory {
    public OauthUserInfo createOauthUserInfo(String provider, Map<String, Object> userAttributes) {
        if (provider.equals("kakao")) {
            return new KakaoUserInfo(userAttributes);
        } else {
            throw new UnsupportedProviderException(ErrorCode.UNSUPPORTED_PROVIDER);
        }
    }
}
