package com.konggogi.veganlife.member.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;

import com.konggogi.veganlife.comment.service.CommentLikeService;
import com.konggogi.veganlife.comment.service.CommentService;
import com.konggogi.veganlife.global.exception.ErrorCode;
import com.konggogi.veganlife.global.exception.NotFoundEntityException;
import com.konggogi.veganlife.global.security.jwt.JwtProvider;
import com.konggogi.veganlife.mealdata.service.MealDataService;
import com.konggogi.veganlife.meallog.service.MealLogService;
import com.konggogi.veganlife.member.controller.dto.request.MemberInfoRequest;
import com.konggogi.veganlife.member.controller.dto.request.MemberProfileRequest;
import com.konggogi.veganlife.member.domain.Gender;
import com.konggogi.veganlife.member.domain.Member;
import com.konggogi.veganlife.member.domain.VegetarianType;
import com.konggogi.veganlife.member.domain.mapper.MemberMapper;
import com.konggogi.veganlife.member.domain.mapper.MemberMapperImpl;
import com.konggogi.veganlife.member.exception.DuplicatedNicknameException;
import com.konggogi.veganlife.member.fixture.MemberFixture;
import com.konggogi.veganlife.member.repository.MemberRepository;
import com.konggogi.veganlife.member.repository.RefreshTokenRepository;
import com.konggogi.veganlife.member.service.dto.MemberLoginDto;
import com.konggogi.veganlife.notification.service.NotificationService;
import com.konggogi.veganlife.post.service.PostLikeService;
import com.konggogi.veganlife.post.service.PostService;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {
    @Mock MemberRepository memberRepository;
    @Mock RefreshTokenRepository refreshTokenRepository;
    @Mock MemberQueryService memberQueryService;
    @Mock NotificationService notificationService;
    @Mock PostLikeService postLikeService;
    @Mock CommentLikeService commentLikeService;
    @Mock PostService postService;
    @Mock CommentService commentService;
    @Mock MealDataService mealDataService;
    @Mock MealLogService mealLogService;
    @Mock JwtProvider jwtProvider;
    @Mock RefreshTokenService refreshTokenService;
    @Spy MemberMapper memberMapper = new MemberMapperImpl();
    @InjectMocks MemberService memberService;
    private final Member member = MemberFixture.DEFAULT_F.getOnlyEmailWithId(1L);

    @Test
    @DisplayName("로그인 - 새로 가입한 경우")
    void loginAndJoinTest() {
        // given
        String accessToken = "accessToken";
        String refreshToken = "refreshToken";
        given(memberRepository.findByEmail(anyString())).willReturn(Optional.empty());
        given(memberRepository.save(any(Member.class))).willReturn(member);
        given(jwtProvider.createToken(anyString())).willReturn(accessToken);
        given(jwtProvider.createRefreshToken(anyString())).willReturn(refreshToken);
        doNothing().when(refreshTokenService).addOrUpdate(anyLong(), anyString());
        // when
        MemberLoginDto result = memberService.login(member.getEmail());
        // then
        assertThat(result.member()).isEqualTo(member);
        assertThat(result.accessToken()).isEqualTo(accessToken);
        assertThat(result.refreshToken()).isEqualTo(refreshToken);
        then(memberRepository).should().save(any(Member.class));
    }

    @Test
    @DisplayName("로그인 - 이미 가입한 경우")
    void loginNotJoinTest() {
        // given
        String accessToken = "accessToken";
        String refreshToken = "refreshToken";
        given(memberRepository.findByEmail(anyString())).willReturn(Optional.of(member));
        given(jwtProvider.createToken(anyString())).willReturn(accessToken);
        given(jwtProvider.createRefreshToken(anyString())).willReturn(refreshToken);
        doNothing().when(refreshTokenService).addOrUpdate(anyLong(), anyString());
        // when
        MemberLoginDto result = memberService.login(member.getEmail());
        // then
        assertThat(result.member()).isEqualTo(member);
        assertThat(result.accessToken()).isEqualTo(accessToken);
        assertThat(result.refreshToken()).isEqualTo(refreshToken);
        then(memberRepository).should(never()).save(any(Member.class));
    }

    @Test
    @DisplayName("회원 삭제")
    void removeMemberTest() {
        // given
        Long memberId = member.getId();
        given(memberQueryService.search(memberId)).willReturn(member);
        doNothing().when(refreshTokenRepository).deleteAllByMemberId(anyLong());
        doNothing().when(postService).removeMemberFromPost(anyLong());
        doNothing().when(commentService).removeMemberFromComment(anyLong());
        doNothing().when(postLikeService).removeMemberFromPostLike(anyLong());
        doNothing().when(commentLikeService).removeMemberFromCommentLike(anyLong());
        doNothing().when(notificationService).removeAll(anyLong());
        doNothing().when(mealDataService).removeAll(anyLong());
        doNothing().when(mealLogService).removeAll(anyLong());
        // when
        memberService.removeMember(memberId);
        // then
        then(memberRepository).should().delete(member);
    }

    @Test
    @DisplayName("회원 정보 수정")
    void modifyMemberInfoTest() {
        // given
        Long memberId = member.getId();
        MemberInfoRequest request =
                new MemberInfoRequest("테스트유저", Gender.M, VegetarianType.LACTO, 1990, 180, 83);
        given(memberRepository.findByNickname(request.nickname())).willReturn(Optional.empty());
        given(memberQueryService.search(memberId)).willReturn(member);
        // when
        Member updatedMember = memberService.modifyMemberInfo(memberId, request);
        // then
        assertThat(updatedMember.getNickname()).isEqualTo(request.nickname());
        assertThat(updatedMember.getGender()).isEqualTo(request.gender());
        assertThat(updatedMember.getVegetarianType()).isEqualTo(request.vegetarianType());
        assertThat(updatedMember.getBirthYear()).isEqualTo(request.birthYear());
        assertThat(updatedMember.getHeight()).isEqualTo(request.height());
        assertThat(updatedMember.getWeight()).isEqualTo(request.weight());
        then(memberRepository).should().findByNickname(request.nickname());
    }

    @Test
    @DisplayName("중복된 닉네임으로 인한 회원 정보 수정 예외 발생")
    void modifyMemberInfoFailTest() {
        // given
        Long memberId = member.getId();
        Member existingMember = MemberFixture.DEFAULT_F.getWithId(1L);
        String nickname = existingMember.getNickname();
        MemberInfoRequest request =
                new MemberInfoRequest(nickname, Gender.F, VegetarianType.VEGAN, 2000, 165, 50);
        given(memberRepository.findByNickname(nickname)).willReturn(Optional.of(existingMember));
        // when, then
        assertThatThrownBy(() -> memberService.modifyMemberInfo(memberId, request))
                .isInstanceOf(DuplicatedNicknameException.class);
        then(memberRepository).should().findByNickname(nickname);
        then(memberRepository).should(never()).save(any(Member.class));
    }

    @Test
    @DisplayName("회원 프로필 수정")
    void modifyMemberProfileTest() {
        // given
        MemberProfileRequest profileRequest =
                new MemberProfileRequest(
                        "nickname", "imageUrl", VegetarianType.LACTO, Gender.M, 1993, 190, 90);
        given(memberRepository.findByNickname(profileRequest.nickname()))
                .willReturn(Optional.empty());
        given(memberQueryService.search(anyLong())).willReturn(member);
        // when
        Member updatedMember = memberService.modifyMemberProfile(member.getId(), profileRequest);
        // then
        assertThat(updatedMember.getNickname()).isEqualTo(profileRequest.nickname());
        assertThat(updatedMember.getProfileImageUrl()).isEqualTo(profileRequest.imageUrl());
        assertThat(updatedMember.getVegetarianType()).isEqualTo(profileRequest.vegetarianType());
        assertThat(updatedMember.getGender()).isEqualTo(profileRequest.gender());
        assertThat(updatedMember.getBirthYear()).isEqualTo(profileRequest.birthYear());
        assertThat(updatedMember.getHeight()).isEqualTo(profileRequest.height());
        assertThat(updatedMember.getWeight()).isEqualTo(profileRequest.weight());
        then(memberRepository).should().findByNickname(anyString());
        then(memberQueryService).should().search(anyLong());
    }

    @Test
    @DisplayName("회원 프로필 수정 시 중복된 닉네임 중복 예외 발생")
    void modifyMemberProfileDuplicatedNicknameTest() {
        // given
        Long memberId = member.getId();
        Member existingMember = MemberFixture.DEFAULT_F.getWithId(1L);
        MemberProfileRequest profileRequest =
                new MemberProfileRequest(
                        existingMember.getNickname(),
                        "imageUrl",
                        VegetarianType.LACTO,
                        Gender.M,
                        1993,
                        190,
                        90);
        given(memberRepository.findByNickname(profileRequest.nickname()))
                .willReturn(Optional.of(existingMember));
        // when, then
        assertThatThrownBy(() -> memberService.modifyMemberProfile(memberId, profileRequest))
                .isInstanceOf(DuplicatedNicknameException.class)
                .hasMessageContaining(ErrorCode.DUPLICATED_NICKNAME.getDescription());
        then(memberRepository).should().findByNickname(anyString());
        then(memberQueryService).should(never()).search(anyLong());
    }

    @Test
    @DisplayName("회원 프로필 수정 시 회원을 찾을 수 없으면 예외 발생")
    void modifyNotMemberProfileTest() {
        // given
        Long memberId = member.getId();
        MemberProfileRequest profileRequest =
                new MemberProfileRequest(
                        "nickname", "imageUrl", VegetarianType.LACTO, Gender.M, 1993, 190, 90);
        given(memberRepository.findByNickname(profileRequest.nickname()))
                .willReturn(Optional.empty());
        given(memberQueryService.search(member.getId()))
                .willThrow(new NotFoundEntityException(ErrorCode.NOT_FOUND_MEMBER));
        // when, then
        assertThatThrownBy(() -> memberService.modifyMemberProfile(memberId, profileRequest))
                .isInstanceOf(NotFoundEntityException.class)
                .hasMessageContaining(ErrorCode.NOT_FOUND_MEMBER.getDescription());
        then(memberRepository).should().findByNickname(anyString());
        then(memberQueryService).should().search(anyLong());
    }
}
