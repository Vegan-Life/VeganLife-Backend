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
    public Gender getGender() {
        String gender = (String) getKakaoAccount().get("gender");
        return gender.equals("female") ? Gender.F : Gender.M;
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
