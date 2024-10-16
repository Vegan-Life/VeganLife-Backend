package com.konggogi.veganlife.meallog.controller;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.konggogi.veganlife.support.docs.ApiDocumentUtils.getDocumentRequest;
import static com.konggogi.veganlife.support.docs.ApiDocumentUtils.getDocumentResponse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.multipart;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.partWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParts;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.amazonaws.HttpMethod;
import com.konggogi.veganlife.global.exception.ErrorCode;
import com.konggogi.veganlife.global.exception.NotFoundEntityException;
import com.konggogi.veganlife.mealdata.domain.MealData;
import com.konggogi.veganlife.mealdata.fixture.MealDataFixture;
import com.konggogi.veganlife.meallog.controller.dto.request.MealAddRequest;
import com.konggogi.veganlife.meallog.controller.dto.request.MealLogAddRequest;
import com.konggogi.veganlife.meallog.controller.dto.request.MealLogModifyRequest;
import com.konggogi.veganlife.meallog.controller.dto.response.MealLogDetailsResponse;
import com.konggogi.veganlife.meallog.controller.dto.response.MealLogListResponse;
import com.konggogi.veganlife.meallog.domain.Meal;
import com.konggogi.veganlife.meallog.domain.MealImage;
import com.konggogi.veganlife.meallog.domain.MealType;
import com.konggogi.veganlife.meallog.domain.mapper.MealLogMapper;
import com.konggogi.veganlife.meallog.domain.mapper.MealLogMapperImpl;
import com.konggogi.veganlife.meallog.fixture.MealFixture;
import com.konggogi.veganlife.meallog.fixture.MealImageFixture;
import com.konggogi.veganlife.meallog.fixture.MealLogFixture;
import com.konggogi.veganlife.meallog.service.MealLogSearchService;
import com.konggogi.veganlife.meallog.service.MealLogService;
import com.konggogi.veganlife.member.domain.Member;
import com.konggogi.veganlife.support.docs.RestDocsTest;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Spy;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.ResultActions;

@WebMvcTest(MealLogController.class)
public class MealLogControllerTest extends RestDocsTest {

    @MockBean MealLogService mealLogService;
    @MockBean MealLogSearchService mealLogSearchService;
    @Spy MealLogMapper mealLogMapper = new MealLogMapperImpl();

    Member member = Member.builder().id(1L).email("test123@test.com").build();
    List<MealData> mealData =
            List.of(
                    MealDataFixture.TOTAL_AMOUNT.get(1L, member),
                    MealDataFixture.AMOUNT_PER_SERVE.get(2L, member),
                    MealDataFixture.TOTAL_AMOUNT.get(3L, member));

    List<MealAddRequest> mealAddRequests =
            List.of(
                    new MealAddRequest(
                            100,
                            100,
                            10,
                            10,
                            10,
                            MealDataFixture.TOTAL_AMOUNT.getWithName(1L, "통밀빵", member).getId()),
                    new MealAddRequest(
                            100,
                            100,
                            10,
                            10,
                            10,
                            MealDataFixture.AMOUNT_PER_SERVE
                                    .getWithName(2L, "통밀크래커", member)
                                    .getId()));

    @Test
    @DisplayName("식사 기록 등록 API")
    void addMealLogTest() throws Exception {

        MealLogAddRequest mealLogAddRequest =
                new MealLogAddRequest(MealType.BREAKFAST, mealAddRequests);
        MockMultipartFile request =
                new MockMultipartFile(
                        "request",
                        "request",
                        MediaType.APPLICATION_JSON_VALUE,
                        toJson(mealLogAddRequest).getBytes());
        List<MockMultipartFile> images =
                List.of(
                        new MockMultipartFile(
                                "images",
                                "image1.png",
                                MediaType.IMAGE_PNG_VALUE,
                                "image1".getBytes()),
                        new MockMultipartFile(
                                "images",
                                "image2.png",
                                MediaType.IMAGE_JPEG_VALUE,
                                "image2".getBytes()));

        ResultActions perform =
                mockMvc.perform(
                        multipart("/api/v1/meal-log")
                                .file(images.get(0))
                                .file(images.get(1))
                                .file(request)
                                .headers(authorizationHeader()));

        perform.andExpect(status().isCreated());

        perform.andDo(print())
                .andDo(
                        document(
                                "meal-log-add",
                                getDocumentRequest(),
                                getDocumentResponse(),
                                requestHeaders(authorizationDesc()),
                                requestParts(
                                        partWithName("request").description("식단 기록 추가 DTO"),
                                        partWithName("images").description("이미지 파일 리스트"))));
    }

    @Test
    @DisplayName("식사 기록 등록 API Member Not Found 예외")
    void addMealLogMemberNotFoundTest() throws Exception {

        MealLogAddRequest mealLogAddRequest =
                new MealLogAddRequest(MealType.BREAKFAST, mealAddRequests);
        MockMultipartFile request =
                new MockMultipartFile(
                        "request",
                        "request",
                        MediaType.APPLICATION_JSON_VALUE,
                        toJson(mealLogAddRequest).getBytes());
        List<MockMultipartFile> images =
                List.of(
                        new MockMultipartFile(
                                "images",
                                "image1.png",
                                MediaType.IMAGE_PNG_VALUE,
                                "image1".getBytes()),
                        new MockMultipartFile(
                                "images",
                                "image2.png",
                                MediaType.IMAGE_JPEG_VALUE,
                                "image2".getBytes()));

        willThrow(new NotFoundEntityException(ErrorCode.NOT_FOUND_MEMBER))
                .given(mealLogService)
                .add(any(MealLogAddRequest.class), any(), anyLong());

        ResultActions perform =
                mockMvc.perform(
                        multipart("/api/v1/meal-log")
                                .file(images.get(0))
                                .file(images.get(1))
                                .file(request)
                                .headers(authorizationHeader()));

        perform.andExpect(status().isNotFound());

        perform.andDo(print())
                .andDo(document("meal-log-add-member-not-found", getDocumentResponse()));
    }

    @Test
    @DisplayName("식사 기록 등록 API MealData Not Found 예외")
    void addMealLogMealDataNotFoundTest() throws Exception {

        MealLogAddRequest mealLogAddRequest =
                new MealLogAddRequest(MealType.BREAKFAST, mealAddRequests);
        MockMultipartFile request =
                new MockMultipartFile(
                        "request",
                        "request",
                        MediaType.APPLICATION_JSON_VALUE,
                        toJson(mealLogAddRequest).getBytes());
        List<MockMultipartFile> images =
                List.of(
                        new MockMultipartFile(
                                "images",
                                "image1.png",
                                MediaType.IMAGE_PNG_VALUE,
                                "image1".getBytes()),
                        new MockMultipartFile(
                                "images",
                                "image2.png",
                                MediaType.IMAGE_JPEG_VALUE,
                                "image2".getBytes()));

        willThrow(new NotFoundEntityException(ErrorCode.NOT_FOUND_MEAL_DATA))
                .given(mealLogService)
                .add(any(MealLogAddRequest.class), any(), anyLong());

        ResultActions perform =
                mockMvc.perform(
                        multipart("/api/v1/meal-log")
                                .file(images.get(0))
                                .file(images.get(1))
                                .file(request)
                                .headers(authorizationHeader()));

        perform.andExpect(status().isNotFound());

        perform.andDo(print())
                .andDo(document("meal-log-add-meal-data-not-found", getDocumentResponse()));
    }

    @Test
    @DisplayName("식사 기록 목록 조회 API")
    void getMealLogListTest() throws Exception {
        List<String> imageUrls = List.of("image1.png", "image2.png", "image3.png");
        LocalDate date = LocalDate.of(2023, 12, 22);
        List<Meal> meals = mealData.stream().map(MealFixture.DEFAULT::get).toList();
        List<MealImage> mealImages =
                imageUrls.stream().map(MealImageFixture.DEFAULT::getWithImageUrl).toList();
        List<MealLogListResponse> mealLogs =
                List.of(
                        mealLogMapper.toMealLogListResponse(
                                MealLogFixture.BREAKFAST.get(1L, meals, mealImages, member)),
                        mealLogMapper.toMealLogListResponse(
                                MealLogFixture.LUNCH.get(2L, meals, mealImages, member)),
                        mealLogMapper.toMealLogListResponse(
                                MealLogFixture.DINNER_SNACK.get(3L, meals, mealImages, member)));

        given(mealLogSearchService.searchByDate(date, member.getId())).willReturn(mealLogs);

        ResultActions perform =
                mockMvc.perform(
                        get("/api/v1/meal-log")
                                .headers(authorizationHeader())
                                .queryParam("date", LocalDate.of(2023, 12, 22).toString()));

        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[2].id").value(3));

        perform.andDo(print())
                .andDo(
                        document(
                                "meal-log-get-meal-log-list",
                                getDocumentRequest(),
                                getDocumentResponse(),
                                requestHeaders(authorizationDesc()),
                                queryParameters(parameterWithName("date").description("조회 날짜"))));
    }

    @Test
    @DisplayName("식사 기록 상세 조회 API")
    void getMealLogDetailsTest() throws Exception {

        List<String> imageUrls = List.of("image1.png", "image2.png", "image3.png");
        List<Meal> meals = mealData.stream().map(MealFixture.DEFAULT::get).toList();
        List<MealImage> mealImages =
                imageUrls.stream().map(MealImageFixture.DEFAULT::getWithImageUrl).toList();
        MealLogDetailsResponse mealLog =
                mealLogMapper.toMealLogDetailsResponse(
                        MealLogFixture.BREAKFAST.get(1L, meals, mealImages, member));

        given(mealLogSearchService.searchById(1L)).willReturn(mealLog);

        ResultActions perform =
                mockMvc.perform(get("/api/v1/meal-log/{id}", 1L).headers(authorizationHeader()));

        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.mealType").value(MealType.BREAKFAST.name()))
                .andExpect(jsonPath("$.totalIntakeNutrients.calorie").value(300))
                .andExpect(jsonPath("$.totalIntakeNutrients.carbs").value(30))
                .andExpect(jsonPath("$.totalIntakeNutrients.protein").value(30))
                .andExpect(jsonPath("$.totalIntakeNutrients.fat").value(30))
                .andExpect(jsonPath("$.imageUrls.size()").value(3))
                .andExpect(jsonPath("$.meals.size()").value(3));

        perform.andDo(print())
                .andDo(
                        document(
                                "meal-log-get-meal-details",
                                getDocumentRequest(),
                                getDocumentResponse(),
                                requestHeaders(authorizationDesc()),
                                pathParameters(parameterWithName("id").description("식단 기록 id"))));
    }

    @Test
    @DisplayName("식사 기록 상세 조회 API - MealLog Not Found")
    void getMealLogDetailsNotFoundTest() throws Exception {

        given(mealLogSearchService.searchById(1L))
                .willThrow(new NotFoundEntityException(ErrorCode.NOT_FOUND_MEAL_LOG));

        ResultActions perform =
                mockMvc.perform(get("/api/v1/meal-log/{id}", 1L).headers(authorizationHeader()));

        perform.andExpect(status().isNotFound());

        perform.andDo(print())
                .andDo(
                        document(
                                "meal-log-get-meal-details-not-found",
                                getDocumentResponse(),
                                requestHeaders(authorizationDesc())));
    }

    @Test
    @DisplayName("식사 기록 수정 API")
    void modifyMealLogTest() throws Exception {

        MealLogModifyRequest mealLogModifyRequest = new MealLogModifyRequest(mealAddRequests);
        MockMultipartFile request =
                new MockMultipartFile(
                        "request",
                        "request",
                        MediaType.APPLICATION_JSON_VALUE,
                        toJson(mealLogModifyRequest).getBytes());
        List<MockMultipartFile> images =
                List.of(
                        new MockMultipartFile(
                                "images",
                                "image1.png",
                                MediaType.IMAGE_PNG_VALUE,
                                "image1".getBytes()),
                        new MockMultipartFile(
                                "images",
                                "image2.png",
                                MediaType.IMAGE_JPEG_VALUE,
                                "image2".getBytes()));

        ResultActions perform =
                mockMvc.perform(
                        multipart("/api/v1/meal-log/{id}", 1L)
                                .file(images.get(0))
                                .file(images.get(1))
                                .file(request)
                                .with(
                                        requestPostProcessor -> {
                                            requestPostProcessor.setMethod(HttpMethod.PUT.name());
                                            return requestPostProcessor;
                                        })
                                .headers(authorizationHeader()));

        perform.andExpect(status().isCreated());

        perform.andDo(print())
                .andDo(
                        document(
                                "meal-log-modify",
                                getDocumentRequest(),
                                getDocumentResponse(),
                                requestHeaders(authorizationDesc()),
                                pathParameters(parameterWithName("id").description("식단 기록 id")),
                                requestParts(
                                        partWithName("request").description("식단 기록 수정 DTO"),
                                        partWithName("images").description("이미지 파일 리스트"))));
    }

    @Test
    @DisplayName("식사 기록 수정 API Member Not Found 예외")
    void modifyMealLogMemberNotFoundTest() throws Exception {

        MealLogModifyRequest mealLogModifyRequest = new MealLogModifyRequest(mealAddRequests);
        MockMultipartFile request =
                new MockMultipartFile(
                        "request",
                        "request",
                        MediaType.APPLICATION_JSON_VALUE,
                        toJson(mealLogModifyRequest).getBytes());
        List<MockMultipartFile> images =
                List.of(
                        new MockMultipartFile(
                                "images",
                                "image1.png",
                                MediaType.IMAGE_PNG_VALUE,
                                "image1".getBytes()),
                        new MockMultipartFile(
                                "images",
                                "image2.png",
                                MediaType.IMAGE_JPEG_VALUE,
                                "image2".getBytes()));

        willThrow(new NotFoundEntityException(ErrorCode.NOT_FOUND_MEAL_LOG))
                .given(mealLogService)
                .modify(anyLong(), any(MealLogModifyRequest.class), any());

        ResultActions perform =
                mockMvc.perform(
                        multipart("/api/v1/meal-log/{id}", 1L)
                                .file(images.get(0))
                                .file(images.get(1))
                                .file(request)
                                .with(
                                        requestPostProcessor -> {
                                            requestPostProcessor.setMethod(HttpMethod.PUT.name());
                                            return requestPostProcessor;
                                        })
                                .headers(authorizationHeader()));

        perform.andExpect(status().isNotFound());

        perform.andDo(print())
                .andDo(document("meal-log-modify-meal-log-not-found", getDocumentResponse()));
    }

    @Test
    @DisplayName("식사 기록 수정 API MealData Not Found 예외")
    void modifyMealLogMealDataNotFoundTest() throws Exception {

        MealLogModifyRequest mealLogModifyRequest = new MealLogModifyRequest(mealAddRequests);
        MockMultipartFile request =
                new MockMultipartFile(
                        "request",
                        "request",
                        MediaType.APPLICATION_JSON_VALUE,
                        toJson(mealLogModifyRequest).getBytes());
        List<MockMultipartFile> images =
                List.of(
                        new MockMultipartFile(
                                "images",
                                "image1.png",
                                MediaType.IMAGE_PNG_VALUE,
                                "image1".getBytes()),
                        new MockMultipartFile(
                                "images",
                                "image2.png",
                                MediaType.IMAGE_JPEG_VALUE,
                                "image2".getBytes()));

        willThrow(new NotFoundEntityException(ErrorCode.NOT_FOUND_MEAL_DATA))
                .given(mealLogService)
                .modify(anyLong(), any(MealLogModifyRequest.class), any());

        ResultActions perform =
                mockMvc.perform(
                        multipart("/api/v1/meal-log/{id}", 1L)
                                .file(images.get(0))
                                .file(images.get(1))
                                .file(request)
                                .with(
                                        requestPostProcessor -> {
                                            requestPostProcessor.setMethod(HttpMethod.PUT.name());
                                            return requestPostProcessor;
                                        })
                                .headers(authorizationHeader()));

        perform.andExpect(status().isNotFound());

        perform.andDo(print())
                .andDo(document("meal-log-modify-meal-data-not-found", getDocumentResponse()));
    }

    @Test
    @DisplayName("식사 기록 삭제 API")
    void removeMealLogTest() throws Exception {

        ResultActions perform =
                mockMvc.perform(delete("/api/v1/meal-log/{id}", 1L).headers(authorizationHeader()));

        perform.andExpect(status().isNoContent());

        perform.andDo(print())
                .andDo(
                        document(
                                "meal-log-delete",
                                getDocumentRequest(),
                                getDocumentResponse(),
                                requestHeaders(authorizationDesc()),
                                pathParameters(parameterWithName("id").description("식단 기록 id"))));
    }
}
