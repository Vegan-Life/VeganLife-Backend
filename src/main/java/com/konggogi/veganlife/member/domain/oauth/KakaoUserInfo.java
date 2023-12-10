package com.konggogi.veganlife.member.domain.oauth;


import java.util.Map;

public class KakaoUserInfo extends OauthUserInfo {
    public KakaoUserInfo(Map<String, Object> attributes) {
        super(attributes);
    }

    @Override
    public String getEmail() {
        return (String) getKakaoAccount().get("email");
    }

    @Override
    public String getPhoneNumber() {
        return (String) getKakaoAccount().get("phone_number");
    }

    @Override
    public String getBirthYear() {
        return (String) getKakaoAccount().get("birthyear");
    }

    private Map<String, Object> getKakaoAccount() {
        return (Map<String, Object>) getAttributes().get("kakao_account");
    }
}
