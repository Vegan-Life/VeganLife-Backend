package com.konggogi.veganlife.support.docs;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.konggogi.veganlife.config.MapStructConfig;
import com.konggogi.veganlife.global.security.filter.JwtAuthenticationFilter;
import com.konggogi.veganlife.support.security.MockSecurityFilter;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.headers.HeaderDescriptor;
import org.springframework.restdocs.request.ParameterDescriptor;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
@Import({RestDocsConfiguration.class, MapStructConfig.class})
@AutoConfigureRestDocs
@ActiveProfiles("test")
@WebMvcTest
public abstract class RestDocsTest {

    @Autowired private ObjectMapper objectMapper;
    @MockBean private JwtAuthenticationFilter jwtAuthenticationFilter;
    protected MockMvc mockMvc;

    @BeforeEach
    public void setMockMvc(
            WebApplicationContext context, RestDocumentationContextProvider provider) {
        mockMvc =
                MockMvcBuilders.webAppContextSetup(context)
                        .apply(
                                documentationConfiguration(provider)
                                        .uris()
                                        .withScheme("https")
                                        .withHost("dev.konggogi.store"))
                        .apply(springSecurity(new MockSecurityFilter()))
                        .addFilter(new CharacterEncodingFilter("UTF-8", true))
                        .alwaysDo(print())
                        .alwaysDo(document("api/v1"))
                        .build();
    }

    protected String toJson(Object value) throws JsonProcessingException {
        return objectMapper.writeValueAsString(value);
    }

    /** Authorization Header만 단독으로 필요한 경우 */
    protected HttpHeaders authorizationHeader() {
        return new HttpHeaders(
                new LinkedMultiValueMap<>(Map.of("Authorization", List.of("Bearer AccessToken"))));
    }

    /**
     * Authorization Header와 다른 Header를 함께 사용하는 경우
     *
     * @param headers 추가적으로 사용할 Header
     */
    protected HttpHeaders withAuthorizationHeader(Map<String, List<String>> headers) {
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>(headers);
        map.put("Authorization", List.of("Bearer AccessToken"));
        return new HttpHeaders(map);
    }

    protected HeaderDescriptor authorizationDesc() {
        return headerWithName("Authorization").description("Bearer AccessToken을 담는 헤더");
    }

    protected ParameterDescriptor pageDesc() {
        return parameterWithName("page").description("페이지 번호");
    }

    protected ParameterDescriptor sizeDesc() {
        return parameterWithName("size").description("페이지 사이즈");
    }
}
