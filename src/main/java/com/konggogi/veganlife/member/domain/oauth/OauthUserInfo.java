package com.konggogi.veganlife.member.domain.oauth;


import com.konggogi.veganlife.member.service.dto.UserInfo;
import java.util.Map;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public abstract class OauthUserInfo {
    private final Map<String, Object> attributes;

    public abstract UserInfo getUserInfo();
}
