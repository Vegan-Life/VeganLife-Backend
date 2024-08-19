package com.konggogi.veganlife.member.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.*;

import com.konggogi.veganlife.comment.service.CommentLikeService;
import com.konggogi.veganlife.comment.service.CommentService;
import com.konggogi.veganlife.global.AwsS3Uploader;
import com.konggogi.veganlife.global.domain.AwsS3Folders;
import com.konggogi.veganlife.global.exception.ErrorCode;
import com.konggogi.veganlife.global.security.jwt.JwtProvider;
import com.konggogi.veganlife.mealdata.service.MealDataService;
import com.konggogi.veganlife.meallog.service.MealLogService;
import com.konggogi.veganlife.member.controller.dto.request.ProfileModifyRequest;
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
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

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
    @Mock AwsS3Uploader awsS3Uploader;
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
        given(jwtProvider.createAccessToken(anyString())).willReturn(accessToken);
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
        given(jwtProvider.createAccessToken(anyString())).willReturn(accessToken);
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
    @DisplayName("회원 프로필 수정")
    void modifyMemberProfileTest() {
        // given
        ProfileModifyRequest request =
                new ProfileModifyRequest("nickname", VegetarianType.LACTO, Gender.M, 1993, 190, 90);
        given(memberRepository.findByNickname(request.nickname())).willReturn(Optional.empty());
        given(memberQueryService.search(anyLong())).willReturn(member);
        MultipartFile profileImage =
                new MockMultipartFile(
                        "image",
                        "profileImage1.png",
                        MediaType.IMAGE_PNG_VALUE,
                        "profileImage1.png".getBytes());
        willReturn("profileImage1.png")
                .given(awsS3Uploader)
                .uploadFile(eq(AwsS3Folders.PROFILE), any());
        // when
        Member update = memberService.modifyProfile(member.getId(), request, profileImage);
        // then
        assertThat(update.getNickname()).isEqualTo(request.nickname());
        assertThat(update.getVegetarianType()).isEqualTo(request.vegetarianType());
        assertThat(update.getGender()).isEqualTo(request.gender());
        assertThat(update.getBirthYear()).isEqualTo(request.birthYear());
        assertThat(update.getHeight()).isEqualTo(request.height());
        assertThat(update.getWeight()).isEqualTo(request.weight());
        then(memberRepository).should().findByNickname(anyString());
        then(memberQueryService).should().search(anyLong());
    }

    @Test
    @DisplayName("회원 프로필 수정 시 중복된 닉네임 중복 예외 발생")
    void modifyMemberProfileDuplicatedNicknameTest() {
        // given
        Member existing = MemberFixture.DEFAULT_F.getWithId(1L);
        Member other = MemberFixture.DEFAULT_M.getWithId(2L);
        String newNickname = other.getNickname();
        ProfileModifyRequest request =
                new ProfileModifyRequest(
                        newNickname, VegetarianType.LACTO, Gender.M, 1993, 190, 90);
        given(memberQueryService.search(anyLong())).willReturn(existing);
        given(memberRepository.findByNickname(anyString())).willReturn(Optional.of(other));
        // when, then
        assertThatThrownBy(() -> memberService.modifyProfile(existing.getId(), request, null))
                .isInstanceOf(DuplicatedNicknameException.class)
                .hasMessageContaining(ErrorCode.DUPLICATED_NICKNAME.getDescription());
        then(memberRepository).should().findByNickname(anyString());
    }

    @Test
    @DisplayName("회원 프로필 수정 시 기존 닉네임이 NULL인 경우 종복 검사")
    void modifyMemberProfileExistingNicknameIsNULL() {
        // given
        Long memberId = member.getId();
        ProfileModifyRequest request =
                new ProfileModifyRequest(
                        "newNickname", VegetarianType.LACTO, Gender.M, 1993, 190, 90);
        given(memberQueryService.search(anyLong())).willReturn(member);
        given(memberRepository.findByNickname(anyString())).willReturn(Optional.empty());
        // when
        memberService.modifyProfile(memberId, request, null);
        // then
        then(memberRepository).should().findByNickname(anyString());
    }

    @Test
    @DisplayName("회원 프로필 수정 시 기존 닉네임과 같으면 중복 검사 하지 않음")
    void modifyMemberProfileNotChangeNickname() {
        // given
        Member existing = MemberFixture.DEFAULT_F.getWithId(1L);
        ProfileModifyRequest request =
                new ProfileModifyRequest(
                        existing.getNickname(), VegetarianType.LACTO, Gender.M, 1993, 190, 90);
        given(memberQueryService.search(anyLong())).willReturn(existing);
        // when
        memberService.modifyProfile(existing.getId(), request, null);
        // then
        then(memberRepository).should(never()).findByNickname(anyString());
    }
}
