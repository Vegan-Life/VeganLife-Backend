package com.konggogi.veganlife.member.domain.oauth;


import com.konggogi.veganlife.member.service.dto.UserInfo;
import java.util.Map;

public class KakaoUserInfo extends OauthUserInfo {
    private static String KAKAO_ACCOUNT = "kakao_account";
    private String id;

    public KakaoUserInfo(Map<String, Object> attributes) {
        super(Map.of(KAKAO_ACCOUNT, attributes.get(KAKAO_ACCOUNT)));
        this.id = String.valueOf(attributes.get("id"));
    }

    @Override
    public UserInfo getUserInfo() {

        return new UserInfo(
                OauthProvider.KAKAO,
                String.valueOf(id),
                String.valueOf(getAttributes().get("email")));
    }
}
