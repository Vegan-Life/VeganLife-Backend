package com.konggogi.veganlife.meallog;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.konggogi.veganlife.mealdata.controller.dto.request.MealDataAddRequest;
import com.konggogi.veganlife.mealdata.domain.mapper.MealDataMapper;
import com.konggogi.veganlife.mealdata.repository.MealDataRepository;
import com.konggogi.veganlife.meallog.controller.dto.request.MealAddRequest;
import com.konggogi.veganlife.meallog.controller.dto.request.MealLogAddRequest;
import com.konggogi.veganlife.meallog.controller.dto.request.MealLogModifyRequest;
import com.konggogi.veganlife.meallog.domain.MealType;
import com.konggogi.veganlife.support.restassured.IntegrationTest;
import com.konggogi.veganlife.support.utils.DateTimeImpl;
import io.restassured.path.json.JsonPath;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

public class MealLogIntegrationTest extends IntegrationTest {

    @Autowired MealDataRepository mealDataRepository;
    @Autowired MealDataMapper mealDataMapper;

    @BeforeEach
    void setup() {
        List<MealDataAddRequest> requests =
                List.of(
                        new MealDataAddRequest("통밀빵", 300, 100, 200, 40, 7, 3, "g"),
                        new MealDataAddRequest("통밀크래커", 300, 100, 200, 40, 7, 3, "g"),
                        new MealDataAddRequest("가지볶음", 300, 100, 200, 40, 7, 3, "g"));
        requests.stream()
                .map(request -> mealDataMapper.toEntity(request, member))
                .forEach(mealDataRepository::save);
    }

    @Test
    @DisplayName("식단 기록 등록 테스트")
    void addMealLogTest() throws Exception {

        List<MealAddRequest> meals =
                List.of(
                        new MealAddRequest(200, 180, 30, 10, 5, 1L),
                        new MealAddRequest(200, 180, 30, 10, 5, 2L),
                        new MealAddRequest(200, 180, 30, 10, 5, 3L));
        List<String> imageUrls = List.of("/image1.png", "/image2.png", "/image3.png");
        MealLogAddRequest request = new MealLogAddRequest(MealType.BREAKFAST, meals, imageUrls);

        given().log()
                .all()
                .header(AUTHORIZATION, getAccessToken())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(toJson(request))
                .when()
                .post("/api/v1/meal-log")
                .then()
                .log()
                .all()
                .statusCode(HttpStatus.CREATED.value());
    }

    @Test
    @DisplayName("식단 기록 목록 조회 테스트")
    void getMealLogListTest() throws Exception {

        List<MealAddRequest> meals =
                List.of(
                        new MealAddRequest(200, 180, 30, 10, 5, 1L),
                        new MealAddRequest(200, 180, 30, 10, 5, 2L),
                        new MealAddRequest(200, 180, 30, 10, 5, 3L));
        List<String> imageUrls =
                List.of("/image1.png", "/image2.png", "/image3.png", "/image4.png");
        List<MealLogAddRequest> requests =
                List.of(
                        new MealLogAddRequest(MealType.BREAKFAST, meals, imageUrls),
                        new MealLogAddRequest(MealType.LUNCH, meals, imageUrls),
                        new MealLogAddRequest(MealType.LUNCH_SNACK, meals, imageUrls));
        for (MealLogAddRequest request : requests) {
            addMealLog(request);
        }

        JsonPath response =
                given().log()
                        .all()
                        .header(AUTHORIZATION, getAccessToken())
                        .param("date", DateTimeImpl.now.toString())
                        .when()
                        .get("/api/v1/meal-log")
                        .then()
                        .log()
                        .all()
                        .statusCode(HttpStatus.OK.value())
                        .extract()
                        .jsonPath();

        assertAll(
                () -> {
                    assertThat(response.getList("")).hasSize(3);
                    assertThat(response.getLong("[0].id")).isEqualTo(1L);
                    assertThat(response.getString("[0].mealType")).isEqualTo("BREAKFAST");
                    assertThat(response.getString("[0].thumbnailUrl")).isEqualTo("/image1.png");
                    assertThat(response.getInt("[0].totalCalorie")).isEqualTo(180 * 3);
                });
    }

    @Test
    @DisplayName("식단 기록 상세 조회 테스트")
    void getMealLogDetailsTest() throws Exception {

        List<MealAddRequest> meals =
                List.of(
                        new MealAddRequest(200, 180, 30, 10, 5, 1L),
                        new MealAddRequest(200, 180, 30, 10, 5, 2L),
                        new MealAddRequest(200, 180, 30, 10, 5, 3L));
        List<String> imageUrls =
                List.of("/image1.png", "/image2.png", "/image3.png", "/image4.png");
        MealLogAddRequest request = new MealLogAddRequest(MealType.BREAKFAST, meals, imageUrls);
        addMealLog(request);

        JsonPath response =
                given().log()
                        .all()
                        .header(AUTHORIZATION, getAccessToken())
                        .when()
                        .get("/api/v1/meal-log/{id}", 1L)
                        .then()
                        .log()
                        .all()
                        .statusCode(HttpStatus.OK.value())
                        .extract()
                        .jsonPath();

        assertAll(
                () -> {
                    assertThat(response.getLong("id")).isEqualTo(1L);
                    assertThat(response.getString("mealType")).isEqualTo("BREAKFAST");
                    assertThat(response.getInt("totalIntakeNutrients.calorie")).isEqualTo(180 * 3);
                    assertThat(response.getInt("totalIntakeNutrients.carbs")).isEqualTo(30 * 3);
                    assertThat(response.getInt("totalIntakeNutrients.protein")).isEqualTo(10 * 3);
                    assertThat(response.getInt("totalIntakeNutrients.fat")).isEqualTo(5 * 3);
                    assertThat(response.getList("imageUrls")).hasSize(4);
                    assertThat(response.getList("meals")).hasSize(3);
                });
    }

    @Test
    @DisplayName("식단 기록 수정 테스트")
    void modifyMealLogTest() throws Exception {

        List<MealAddRequest> meals =
                List.of(
                        new MealAddRequest(200, 180, 30, 10, 5, 1L),
                        new MealAddRequest(200, 180, 30, 10, 5, 2L),
                        new MealAddRequest(200, 180, 30, 10, 5, 3L));
        List<String> imageUrls = List.of("/image1.png", "/image2.png", "/image3.png");
        MealLogAddRequest request = new MealLogAddRequest(MealType.BREAKFAST, meals, imageUrls);
        addMealLog(request);

        List<MealAddRequest> modifiedMeals =
                List.of(
                        new MealAddRequest(200, 200, 30, 10, 5, 1L),
                        new MealAddRequest(300, 220, 30, 10, 5, 2L),
                        new MealAddRequest(400, 240, 30, 10, 5, 3L));
        List<String> modifiedImageUrls = List.of("/image1.png", "/image2.png", "/image4.png");
        MealLogModifyRequest mealLogModifyRequest =
                new MealLogModifyRequest(modifiedMeals, modifiedImageUrls);

        given().log()
                .all()
                .header(AUTHORIZATION, getAccessToken())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(toJson(mealLogModifyRequest))
                .when()
                .put("/api/v1/meal-log/{id}", 1L)
                .then()
                .statusCode(HttpStatus.CREATED.value());
    }

    @Test
    @DisplayName("식단 기록 삭제 테스트")
    void removeMealLogTest() throws Exception {

        List<MealAddRequest> meals =
                List.of(
                        new MealAddRequest(200, 180, 30, 10, 5, 1L),
                        new MealAddRequest(200, 180, 30, 10, 5, 2L),
                        new MealAddRequest(200, 180, 30, 10, 5, 3L));
        List<String> imageUrls = List.of("/image1.png", "/image2.png", "/image3.png");
        MealLogAddRequest request = new MealLogAddRequest(MealType.BREAKFAST, meals, imageUrls);
        addMealLog(request);

        given().log()
                .all()
                .header(AUTHORIZATION, getAccessToken())
                .when()
                .delete("/api/v1/meal-log/{id}", 1L)
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value());
    }

    private void addMealLog(MealLogAddRequest request) throws Exception {

        given().log()
                .all()
                .header(AUTHORIZATION, getAccessToken())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(toJson(request))
                .when()
                .post("/api/v1/meal-log")
                .then()
                .statusCode(HttpStatus.CREATED.value());
    }
}
