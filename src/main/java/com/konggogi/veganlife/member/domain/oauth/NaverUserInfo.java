package com.konggogi.veganlife.member.domain.oauth;


import com.konggogi.veganlife.member.service.dto.UserInfo;
import java.util.Map;

public class NaverUserInfo extends OauthUserInfo {
    private static String NAVER_RESPONSE = "response";

    public NaverUserInfo(Map<String, Object> attributes) {
        super(Map.of(NAVER_RESPONSE, attributes.get(NAVER_RESPONSE)));
    }

    @Override
    public UserInfo getUserInfo() {

        return new UserInfo(
                OauthProvider.NAVER,
                String.valueOf(getAttributes().get("id")),
                String.valueOf(getAttributes().get("email")));
    }
}
