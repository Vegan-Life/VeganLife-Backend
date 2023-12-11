package com.konggogi.veganlife.member.domain.oauth;


import java.util.Map;

public class NaverUserInfo extends OauthUserInfo {
    public NaverUserInfo(Map<String, Object> attributes) {
        super(attributes);
    }

    @Override
    public String getEmail() {
        return (String) getResponse().get("email");
    }

    private Map<String, String> getResponse() {
        return (Map<String, String>) getAttributes().get("response");
    }
}
