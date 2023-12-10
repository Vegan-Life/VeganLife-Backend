package com.konggogi.veganlife.member.service;


import com.konggogi.veganlife.member.domain.oauth.KakaoUserInfo;
import com.konggogi.veganlife.member.domain.oauth.NaverUserInfo;
import com.konggogi.veganlife.member.domain.oauth.OauthProvider;
import com.konggogi.veganlife.member.domain.oauth.OauthUserInfo;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class OauthUserInfoFactory {
    public OauthUserInfo createOauthUserInfo(
            OauthProvider provider, Map<String, Object> userAttributes) {
        return provider == OauthProvider.KAKAO
                ? new KakaoUserInfo(userAttributes)
                : new NaverUserInfo(userAttributes);
    }
}
