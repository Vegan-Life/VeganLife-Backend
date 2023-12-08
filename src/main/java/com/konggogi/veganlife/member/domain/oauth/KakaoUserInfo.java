package com.konggogi.veganlife.member.domain.oauth;


import com.konggogi.veganlife.member.domain.Gender;
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
    public String getProfileImageUrl() {
        return (String) getProfile().get("profile_image_url");
    }

    @Override
    public Gender getGender() {
        String gender = (String) getKakaoAccount().get("gender");
        return gender.equals("female") ? Gender.F : Gender.M;
    }

    @Override
    public String getPhoneNumber() {
        return (String) getKakaoAccount().get("phone_number");
    }

    private Map<String, Object> getKakaoAccount() {
        return (Map<String, Object>) getAttributes().get("kakao_account");
    }

    private Map<String, Object> getProfile() {
        return (Map<String, Object>) getKakaoAccount().get("profile");
    }
}
