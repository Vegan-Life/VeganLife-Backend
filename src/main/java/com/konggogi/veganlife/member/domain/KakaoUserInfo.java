package com.konggogi.veganlife.member.domain;


import java.util.Map;

public record KakaoUserInfo(Map<String, Object> attributes) {

    public String getProfileImageUrl() {
        return (String) getProfile().get("profile_image_url");
    }

    public String getEmail() {
        return (String) getKakaoAccount().get("email");
    }

    private Map<String, Object> getKakaoAccount() {
        return (Map<String, Object>) attributes.get("kakao_account");
    }

    private Map<String, Object> getProfile() {
        return (Map<String, Object>) getKakaoAccount().get("profile");
    }
}
