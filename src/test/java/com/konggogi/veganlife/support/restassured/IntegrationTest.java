package com.konggogi.veganlife.support.restassured;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.konggogi.veganlife.global.security.jwt.JwtProvider;
import com.konggogi.veganlife.member.domain.Gender;
import com.konggogi.veganlife.member.domain.Member;
import com.konggogi.veganlife.member.domain.VegetarianType;
import com.konggogi.veganlife.member.service.MemberService;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

@ExtendWith(DatabaseClearExtension.class)
@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
public class IntegrationTest {

    @Autowired private ObjectMapper objectMapper;
    @Autowired private MemberService memberService;
    @Autowired private JwtProvider jwtProvider;

    protected static final String AUTHORIZATION = "Authorization";

    protected Member member;

    protected String toJson(Object object) throws JsonProcessingException {
        return objectMapper.writeValueAsString(object);
    }

    protected String getAccessToken() {
        member = Member.builder().email("test123@test.com").build();
        member.updateMemberInfo("비건라이프", Gender.F, VegetarianType.VEGAN, 2000, 160, 50);
        memberService.addMember(member.getEmail());

        return jwtProvider.createToken(member.getEmail());
    }
}
