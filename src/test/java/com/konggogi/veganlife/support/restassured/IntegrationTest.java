package com.konggogi.veganlife.support.restassured;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.konggogi.veganlife.config.JpaAuditingConfig;
import com.konggogi.veganlife.global.security.jwt.JwtProvider;
import com.konggogi.veganlife.member.domain.Gender;
import com.konggogi.veganlife.member.domain.Member;
import com.konggogi.veganlife.member.domain.VegetarianType;
import com.konggogi.veganlife.member.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ContextConfiguration;

@ExtendWith(DatabaseClearExtension.class)
@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
@ContextConfiguration(classes = JpaAuditingConfig.class)
public class IntegrationTest {

    @Autowired private ObjectMapper objectMapper;
    @Autowired private MemberRepository memberRepository;
    @Autowired private JwtProvider jwtProvider;

    protected static final String AUTHORIZATION = "Authorization";

    protected Member member;

    protected String toJson(Object object) throws JsonProcessingException {
        return objectMapper.writeValueAsString(object);
    }

    @BeforeEach
    void beforeEach() {
        member = Member.builder().email("test123@test.com").build();
        member.updateMemberInfo("비건라이프", Gender.F, VegetarianType.VEGAN, 2000, 160, 50);
        memberRepository.save(member);
    }

    protected String getAccessToken() {

        return jwtProvider.createToken(member.getEmail());
    }
}
