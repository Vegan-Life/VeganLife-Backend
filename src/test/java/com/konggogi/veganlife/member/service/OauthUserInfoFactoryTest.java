package com.konggogi.veganlife.member.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.konggogi.veganlife.member.domain.oauth.KakaoUserInfo;
import com.konggogi.veganlife.member.domain.oauth.NaverUserInfo;
import com.konggogi.veganlife.member.domain.oauth.OauthProvider;
import com.konggogi.veganlife.member.domain.oauth.OauthUserInfo;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OauthUserInfoFactoryTest {
    @InjectMocks OauthUserInfoFactory oauthUserInfoFactory;

    @Test
    @DisplayName("provider가 kakao이면 KakaoUserInfo 생성")
    void createKakaoUserInfoTest() {
        // given
        Map<String, Object> userAttributes = new HashMap<>();
        userAttributes.put("key", "value");
        // when
        OauthUserInfo userInfo =
                oauthUserInfoFactory.createOauthUserInfo(OauthProvider.KAKAO, userAttributes);
        // then
        assertThat(userInfo).isInstanceOf(KakaoUserInfo.class);
    }

    @Test
    @DisplayName("provider가 naver이면 NaverUserInfo 생성")
    void createNaverUserInfoTest() {
        // given
        Map<String, Object> userAttributes = new HashMap<>();
        userAttributes.put("key", "value");
        // when
        OauthUserInfo userInfo =
                oauthUserInfoFactory.createOauthUserInfo(OauthProvider.NAVER, userAttributes);
        // then
        assertThat(userInfo).isInstanceOf(NaverUserInfo.class);
    }
}
