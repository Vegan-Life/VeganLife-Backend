package com.konggogi.veganlife.member.service;


import com.konggogi.veganlife.global.exception.ErrorCode;
import com.konggogi.veganlife.global.exception.NotFoundEntityException;
import com.konggogi.veganlife.member.controller.dto.request.MemberRegisterRequest;
import com.konggogi.veganlife.member.domain.Member;
import com.konggogi.veganlife.member.domain.mapper.MemberMapper;
import com.konggogi.veganlife.member.exception.DuplicateNicknameException;
import com.konggogi.veganlife.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {
    private final MemberRepository memberRepository;
    private final MemberMapper memberMapper;

    @Transactional
    public Member addMember(MemberRegisterRequest memberRegisterRequest) {
        validateNickname(memberRegisterRequest.nickname());
        Member member = memberMapper.toEntity(memberRegisterRequest);
        return memberRepository.save(member);
    }

    private void validateNickname(String nickname) {
        memberRepository
                .findByNickname(nickname)
                .ifPresent(
                        member -> {
                            throw new DuplicateNicknameException(ErrorCode.DUPLICATED_NICKNAME);
                        });
    }

    @Transactional
    public void removeMember(Long memberId) {
        Member member = validateMemberExist(memberId);
        memberRepository.delete(member);
    }

    private Member validateMemberExist(Long memberId) {
        return memberRepository
                .findById(memberId)
                .orElseThrow(() -> new NotFoundEntityException(ErrorCode.NOT_FOUND_MEMBER));
    }
}
