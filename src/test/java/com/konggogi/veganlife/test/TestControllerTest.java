package com.konggogi.veganlife.test;

import static com.konggogi.veganlife.support.docs.ApiDocumentUtils.getDocumentRequest;
import static com.konggogi.veganlife.support.docs.ApiDocumentUtils.getDocumentResponse;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.konggogi.veganlife.support.docs.RestDocsTest;
import com.konggogi.veganlife.test.controller.TestController;
import com.konggogi.veganlife.test.controller.dto.request.TestRequest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

/** Rest Docs를 설명하기 위한 Test입니다. */
@WebMvcTest(TestController.class)
class TestControllerTest extends RestDocsTest {

    /** Get Method 테스트 Path Variable, Query Param이 있는 경우 */
    @Test
    void getTest() throws Exception {

        /** Path Variable은 get의 parameter로 넣어준다. Query Parameter는 체이닝 메서드를 이용해 정의한다. */
        ResultActions perform =
                mockMvc.perform(get("/api/v1/test/{path}", "vegan").queryParam("query", "life"));

        perform.andExpect(status().isOk());

        perform.andDo(print())
                .andDo(
                        document(
                                "get-test", // API 문서 snippet의 identifier
                                getDocumentRequest(), // request snippet 생성
                                getDocumentResponse(), // response snippet 생성
                                pathParameters( // Path Variable snippet 생성
                                        parameterWithName("path")
                                                .description("Path Variable 입니다.")),
                                queryParameters( // Query Param snippet 생성
                                        parameterWithName("query")
                                                .description("Query Parameter 입니다."))));
    }

    @Test
    void postTest() throws Exception {

        TestRequest request = new TestRequest("Vegan Life!");

        ResultActions perform =
                mockMvc.perform(
                        post("/api/v1/test")
                                .contentType(MediaType.APPLICATION_JSON) // contentType 설정
                                .content(toJson(request))); // 객체를 Json으로 변환하여 전달해야함(toJson 사용)

        perform.andExpect(status().isOk());

        perform.andDo(print())
                .andDo(document("post-test", getDocumentRequest(), getDocumentResponse()));
    }
}
