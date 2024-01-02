package com.konggogi.veganlife.member.service;


import com.konggogi.veganlife.global.security.jwt.JwtProvider;
import com.konggogi.veganlife.member.controller.dto.request.OauthLoginRequest;
import com.konggogi.veganlife.member.controller.dto.request.SignupRequest;
import com.konggogi.veganlife.member.domain.Member;
import com.konggogi.veganlife.member.domain.oauth.OauthProvider;
import com.konggogi.veganlife.member.service.dto.JwtToken;
import com.konggogi.veganlife.member.service.dto.UserInfo;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private final OauthService oauthService;
    private final MemberQueryService memberQueryService;
    private final MemberService memberService;
    private final JwtProvider jwtProvider;

    public Optional<JwtToken> login(OauthProvider provider, OauthLoginRequest request) {
        UserInfo userInfo = oauthService.getUserInfo(provider, request.accessToken());
        /**
         * 로그인을 시도하는 사용자의 정보를 provider의 리소스 서버에서 가져온다.
         *
         * <p>사용자의 정보가 애플리케이션 서버의 DB에 존재한다면 로그인에 성공한 것이다. access token과 refresh token을 발급한다.
         *
         * <p>사용자의 정보가 애플리케이션 서버의 DB에 존재하지 않는다는건, 회원가입이 필요하다는 의미이다. 빈 Optional 객체를 반환한다.
         */
        return memberQueryService
                .searchLoggedinMember(userInfo.provider(), userInfo.id())
                .map(
                        member -> {
                            String accessToken = jwtProvider.createToken(member.getId());
                            String refreshToken = jwtProvider.createRefreshToken(member.getId());
                            memberService.saveRefreshToken(member.getId(), refreshToken);
                            return Optional.of(new JwtToken(accessToken, refreshToken));
                        })
                .orElseGet(Optional::empty);
    }

    public JwtToken signup(OauthProvider provider, SignupRequest request) {

        UserInfo userInfo = oauthService.getUserInfo(provider, request.accessToken());
        Member member = memberService.addMember(userInfo, request);
        String accessToken = jwtProvider.createToken(member.getId());
        String refreshToken = jwtProvider.createRefreshToken(member.getId());
        memberService.saveRefreshToken(member.getId(), refreshToken);

        return new JwtToken(accessToken, refreshToken);
    }

    public JwtToken reissueToken(Long memberId) {

        String accessToken = jwtProvider.createToken(memberId);
        String refreshToken = jwtProvider.createRefreshToken(memberId);
        return new JwtToken(accessToken, refreshToken);
    }
}
