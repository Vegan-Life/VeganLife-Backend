package com.konggogi.veganlife.member.domain.oauth;


import java.util.Map;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public abstract class OauthUserInfo {
    private final Map<String, Object> attributes;

    public abstract String getEmail();

    public abstract String getPhoneNumber();

    public abstract String getBirthYear();
}
