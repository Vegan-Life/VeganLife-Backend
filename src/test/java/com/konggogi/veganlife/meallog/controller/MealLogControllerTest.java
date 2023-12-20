package com.konggogi.veganlife.meallog.controller;

import static com.konggogi.veganlife.support.docs.ApiDocumentUtils.getDocumentRequest;
import static com.konggogi.veganlife.support.docs.ApiDocumentUtils.getDocumentResponse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.konggogi.veganlife.global.exception.ErrorCode;
import com.konggogi.veganlife.global.exception.NotFoundEntityException;
import com.konggogi.veganlife.mealdata.domain.IntakeUnit;
import com.konggogi.veganlife.mealdata.fixture.MealDataFixture;
import com.konggogi.veganlife.meallog.controller.dto.request.MealAddRequest;
import com.konggogi.veganlife.meallog.controller.dto.request.MealLogAddRequest;
import com.konggogi.veganlife.meallog.domain.MealType;
import com.konggogi.veganlife.meallog.service.MealLogService;
import com.konggogi.veganlife.member.domain.Member;
import com.konggogi.veganlife.support.docs.RestDocsTest;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

@WebMvcTest(MealLogController.class)
public class MealLogControllerTest extends RestDocsTest {

    @MockBean MealLogService mealLogService;

    Member member = Member.builder().id(1L).email("test123@test.com").build();

    List<MealAddRequest> mealAddRequests =
            List.of(
                    new MealAddRequest(
                            "통밀빵",
                            100,
                            IntakeUnit.G,
                            100,
                            10,
                            10,
                            10,
                            MealDataFixture.MEAL.getWithName(1L, "통밀빵", member).getId()),
                    new MealAddRequest(
                            "통밀크래커",
                            100,
                            IntakeUnit.G,
                            100,
                            10,
                            10,
                            10,
                            MealDataFixture.PROCESSED.getWithName(2L, "통밀크래커", member).getId()));

    @Test
    @DisplayName("식사 기록 등록 API")
    void addMealLogTest() throws Exception {

        MealLogAddRequest mealLogAddRequest =
                new MealLogAddRequest(MealType.BREAKFAST, mealAddRequests);

        ResultActions perform =
                mockMvc.perform(
                        post("/api/v1/meal-log")
                                .headers(authorizationHeader())
                                .content(toJson(mealLogAddRequest))
                                .contentType(MediaType.APPLICATION_JSON));

        perform.andExpect(status().isCreated());

        perform.andDo(print())
                .andDo(
                        document(
                                "meal-log-add",
                                getDocumentRequest(),
                                getDocumentResponse(),
                                requestHeaders(authorizationDesc())));
    }

    @Test
    @DisplayName("식사 기록 등록 API Member Not Found 예외")
    void addMealLogMemberNotFoundTest() throws Exception {

        MealLogAddRequest mealLogAddRequest =
                new MealLogAddRequest(MealType.BREAKFAST, mealAddRequests);

        willThrow(new NotFoundEntityException(ErrorCode.NOT_FOUND_MEMBER))
                .given(mealLogService)
                .add(any(MealLogAddRequest.class), anyLong());

        ResultActions perform =
                mockMvc.perform(
                        post("/api/v1/meal-log")
                                .headers(authorizationHeader())
                                .content(toJson(mealLogAddRequest))
                                .contentType(MediaType.APPLICATION_JSON));

        perform.andExpect(status().isNotFound());

        perform.andDo(print())
                .andDo(document("meal-log-add-member-not-found", getDocumentResponse()));
    }

    @Test
    @DisplayName("식사 기록 등록 API MealData Not Found 예외")
    void addMealLogMealDataNotFoundTest() throws Exception {

        MealLogAddRequest mealLogAddRequest =
                new MealLogAddRequest(MealType.BREAKFAST, mealAddRequests);

        willThrow(new NotFoundEntityException(ErrorCode.NOT_FOUND_MEAL_DATA))
                .given(mealLogService)
                .add(any(MealLogAddRequest.class), anyLong());

        ResultActions perform =
                mockMvc.perform(
                        post("/api/v1/meal-log")
                                .headers(authorizationHeader())
                                .content(toJson(mealLogAddRequest))
                                .contentType(MediaType.APPLICATION_JSON));

        perform.andExpect(status().isNotFound());

        perform.andDo(print())
                .andDo(document("meal-log-add-meal-data-not-found", getDocumentResponse()));
    }
}
