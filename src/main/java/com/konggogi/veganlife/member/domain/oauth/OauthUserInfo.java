package com.konggogi.veganlife.member.domain.oauth;


import com.konggogi.veganlife.member.domain.Gender;
import java.util.Map;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public abstract class OauthUserInfo {
    private final Map<String, Object> attributes;

    public abstract String getEmail();

    public abstract String getProfileImageUrl();

    public abstract Gender getGender();

    public abstract String getPhoneNumber();
}
