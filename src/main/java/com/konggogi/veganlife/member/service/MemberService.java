package com.konggogi.veganlife.member.service;


import com.konggogi.veganlife.global.exception.ErrorCode;
import com.konggogi.veganlife.global.exception.NotFoundEntityException;
import com.konggogi.veganlife.member.domain.Member;
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

    @Transactional
    public Member addMember(String email) {
        return memberRepository
                .findByEmail(email)
                .orElseGet(
                        () -> {
                            Member member = Member.builder().email(email).build();
                            return memberRepository.save(member);
                        });
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

    public Member search(Long memberId) {
        return validateMemberExist(memberId);
    }

    private Member validateMemberExist(Long memberId) {
        return memberRepository
                .findById(memberId)
                .orElseThrow(() -> new NotFoundEntityException(ErrorCode.NOT_FOUND_MEMBER));
    }
}
