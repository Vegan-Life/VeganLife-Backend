package com.konggogi.veganlife.member.domain.oauth;


import com.konggogi.veganlife.member.domain.Gender;
import java.util.Map;

public class NaverUserInfo extends OauthUserInfo {
    public NaverUserInfo(Map<String, Object> attributes) {
        super(attributes);
    }

    @Override
    public String getEmail() {
        return (String) getResponse().get("email");
    }

    @Override
    public Gender getGender() {
        // TODO 결과값이 'U'인 경우 처리 필요
        String gender = (String) getResponse().get("gender");
        return gender.equals("F") ? Gender.F : Gender.M;
    }

    @Override
    public String getPhoneNumber() {
        return (String) getResponse().get("mobile");
    }

    @Override
    public String getBirthYear() {
        return (String) getResponse().get("birthyear");
    }

    private Map<String, String> getResponse() {
        return (Map<String, String>) getAttributes().get("response");
    }
}
