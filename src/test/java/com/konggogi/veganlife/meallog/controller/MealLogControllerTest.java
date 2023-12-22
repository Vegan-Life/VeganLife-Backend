package com.konggogi.veganlife.meallog.controller;

import static com.konggogi.veganlife.support.docs.ApiDocumentUtils.getDocumentRequest;
import static com.konggogi.veganlife.support.docs.ApiDocumentUtils.getDocumentResponse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.konggogi.veganlife.global.exception.ErrorCode;
import com.konggogi.veganlife.global.exception.NotFoundEntityException;
import com.konggogi.veganlife.mealdata.domain.IntakeUnit;
import com.konggogi.veganlife.mealdata.domain.MealData;
import com.konggogi.veganlife.mealdata.fixture.MealDataFixture;
import com.konggogi.veganlife.meallog.controller.dto.request.MealAddRequest;
import com.konggogi.veganlife.meallog.controller.dto.request.MealLogAddRequest;
import com.konggogi.veganlife.meallog.domain.Meal;
import com.konggogi.veganlife.meallog.domain.MealImage;
import com.konggogi.veganlife.meallog.domain.MealLog;
import com.konggogi.veganlife.meallog.domain.MealType;
import com.konggogi.veganlife.meallog.domain.mapper.MealLogMapper;
import com.konggogi.veganlife.meallog.domain.mapper.MealLogMapperImpl;
import com.konggogi.veganlife.meallog.fixture.MealFixture;
import com.konggogi.veganlife.meallog.fixture.MealImageFixture;
import com.konggogi.veganlife.meallog.fixture.MealLogFixture;
import com.konggogi.veganlife.meallog.service.MealLogQueryService;
import com.konggogi.veganlife.meallog.service.MealLogService;
import com.konggogi.veganlife.meallog.service.dto.MealLogList;
import com.konggogi.veganlife.member.domain.Member;
import com.konggogi.veganlife.support.docs.RestDocsTest;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Spy;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

@WebMvcTest(MealLogController.class)
public class MealLogControllerTest extends RestDocsTest {

    @MockBean MealLogService mealLogService;
    @MockBean MealLogQueryService mealLogQueryService;
    @Spy MealLogMapper mealLogMapper = new MealLogMapperImpl();

    Member member = Member.builder().id(1L).email("test123@test.com").build();
    List<MealData> mealData =
            List.of(
                    MealDataFixture.MEAL.get(member),
                    MealDataFixture.MEAL.get(member),
                    MealDataFixture.MEAL.get(member));

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

    List<String> imageUrls = List.of("image1.png", "image2.png", "image3.png");

    @Test
    @DisplayName("식사 기록 등록 API")
    void addMealLogTest() throws Exception {

        MealLogAddRequest mealLogAddRequest =
                new MealLogAddRequest(MealType.BREAKFAST, mealAddRequests, imageUrls);

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
                new MealLogAddRequest(MealType.BREAKFAST, mealAddRequests, imageUrls);

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
                new MealLogAddRequest(MealType.BREAKFAST, mealAddRequests, imageUrls);

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

    @Test
    @DisplayName("식사 기록 목록 조회 API")
    void getMealLogListTest() throws Exception {

        LocalDate date = LocalDate.of(2023, 12, 22);
        List<MealLogList> mealLogs = IntStream.of(0, 3, 6).mapToObj(this::getMealLogList).toList();

        given(mealLogQueryService.searchByDate(date, member.getId())).willReturn(mealLogs);

        ResultActions perform =
                mockMvc.perform(
                        get("/api/v1/meal-log")
                                .headers(authorizationHeader())
                                .queryParam("date", LocalDate.of(2023, 12, 22).toString()));

        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[1].id").value(4))
                .andExpect(jsonPath("$[2].id").value(7));

        perform.andDo(print())
                .andDo(
                        document(
                                "meal-log-get-meal-log-list",
                                getDocumentRequest(),
                                getDocumentResponse(),
                                requestHeaders(authorizationDesc()),
                                queryParameters(parameterWithName("date").description("조회 날짜"))));
    }

    private MealLogList getMealLogList(long v) {
        List<Meal> meals =
                LongStream.range(v, v + 3)
                        .mapToObj(
                                idx ->
                                        MealFixture.DEFAULT.get(
                                                idx + 1, mealData.get((int) idx % 3)))
                        .toList();
        List<MealImage> mealImages =
                LongStream.range(v, v + 3)
                        .mapToObj(idx -> MealImageFixture.DEFAULT.get(idx + 1))
                        .toList();
        MealLog mealLog =
                MealLogFixture.BREAKFAST.getWithDate(
                        v + 1, LocalDate.of(2022, 12, 22), meals, mealImages, member);
        return new MealLogList(mealLog, "image" + (v + 1) + ".png", 300);
    }
}
