package com.konggogi.veganlife.mealdata;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import com.konggogi.veganlife.mealdata.controller.dto.request.MealDataAddRequest;
import com.konggogi.veganlife.mealdata.domain.IntakeUnit;
import com.konggogi.veganlife.mealdata.domain.MealDataType;
import com.konggogi.veganlife.mealdata.domain.mapper.MealDataMapper;
import com.konggogi.veganlife.mealdata.service.MealDataQueryService;
import com.konggogi.veganlife.mealdata.service.MealDataService;
import com.konggogi.veganlife.support.restassured.IntegrationTest;
import io.restassured.path.json.JsonPath;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

public class MealDataIntegrationTest extends IntegrationTest {

    @Autowired MealDataQueryService mealDataQueryService;
    @Autowired MealDataService mealDataService;
    @Autowired MealDataMapper mealDataMapper;

    @Test
    @DisplayName("식품 데이터 등록")
    void addMealDataTest() throws Exception {

        MealDataAddRequest request =
                new MealDataAddRequest("통밀빵", 300, 100, 200, 40, 7, 3, IntakeUnit.G);
        given().log()
                .all()
                .header(AUTHORIZATION, getAccessToken())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(toJson(request))
                .when()
                .post("/api/v1/meal-data")
                .then()
                .log()
                .all()
                .statusCode(HttpStatus.CREATED.value());
    }

    @Test
    @DisplayName("id 기반 식품 데이터 상세 조회")
    void getMealDataDetailsTest() throws Exception {

        MealDataAddRequest request =
                new MealDataAddRequest("통밀빵", 300, 100, 200, 40, 7, 3, IntakeUnit.G);
        addMealData(request);

        JsonPath response =
                given().log()
                        .all()
                        .header(AUTHORIZATION, getAccessToken())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .body(toJson(request))
                        .when()
                        .get("/api/v1/meal-data/{id}", 1L)
                        .then()
                        .log()
                        .all()
                        .statusCode(HttpStatus.OK.value())
                        .extract()
                        .jsonPath();

        assertAll(
                () -> assertThat(response.getLong("id")).isEqualTo(1L),
                () -> assertThat(response.getString("name")).isEqualTo("통밀빵"),
                () ->
                        assertThat(response.getString("type"))
                                .isEqualTo(MealDataType.AMOUNT_PER_SERVE.name()),
                () -> assertThat(response.getInt("amount")).isEqualTo(300),
                () -> assertThat(response.getInt("amountPerServe")).isEqualTo(100),
                () -> assertThat(response.getDouble("caloriePerUnit")).isEqualTo(200D / 100),
                () -> assertThat(response.getDouble("carbsPerUnit")).isEqualTo(40D / 100),
                () -> assertThat(response.getDouble("proteinPerUnit")).isEqualTo(7D / 100),
                () -> assertThat(response.getDouble("fatPerUnit")).isEqualTo(3D / 100),
                () -> assertThat(response.getString("intakeUnit")).isEqualTo(IntakeUnit.G.name()));
    }

    @Test
    @DisplayName("키워드 기반 식품 데이터 목록 검색")
    void getMealDataListTest() throws Exception {

        List<MealDataAddRequest> requests =
                List.of(
                        new MealDataAddRequest("통밀빵", 300, 100, 200, 40, 7, 3, IntakeUnit.G),
                        new MealDataAddRequest("통밀크래커", 300, 100, 200, 40, 7, 3, IntakeUnit.G),
                        new MealDataAddRequest("가지볶음", 300, 100, 200, 40, 7, 3, IntakeUnit.G));
        for (MealDataAddRequest request : requests) {
            addMealData(request);
        }

        JsonPath response =
                given().log()
                        .all()
                        .header(AUTHORIZATION, getAccessToken())
                        .param("keyword", "통")
                        .param("ownerType", "MEMBER")
                        .param("page", 0)
                        .param("size", 20)
                        .when()
                        .get("api/v1/meal-data")
                        .then()
                        .log()
                        .all()
                        .statusCode(HttpStatus.OK.value())
                        .extract()
                        .jsonPath();

        assertAll(
                () -> assertThat(response.getInt("totalElements")).isEqualTo(2),
                () -> assertThat(response.getLong("content[0].id")).isEqualTo(1L),
                () -> assertThat(response.getString("content[0].name")).isEqualTo("통밀빵"));
    }

    @Test
    @DisplayName("키워드 기반 식품 데이터 목록 검색 - 키워드가 없을 경우 빈 리스트 반환")
    void getMealDataListWithoutKeywordTest() throws Exception {

        List<MealDataAddRequest> requests =
                List.of(
                        new MealDataAddRequest("통밀빵", 300, 100, 200, 40, 7, 3, IntakeUnit.G),
                        new MealDataAddRequest("통밀크래커", 300, 100, 200, 40, 7, 3, IntakeUnit.G),
                        new MealDataAddRequest("가지볶음", 300, 100, 200, 40, 7, 3, IntakeUnit.G));
        for (MealDataAddRequest request : requests) {
            addMealData(request);
        }

        JsonPath response =
                given().log()
                        .all()
                        .header(AUTHORIZATION, getAccessToken())
                        .param("ownerType", "MEMBER")
                        .param("page", 0)
                        .param("size", 20)
                        .when()
                        .get("api/v1/meal-data")
                        .then()
                        .log()
                        .all()
                        .statusCode(HttpStatus.OK.value())
                        .extract()
                        .jsonPath();

        assertAll(
                () -> assertThat(response.getInt("totalElements")).isEqualTo(0),
                () -> assertThat(response.getList("content")).isEmpty());
    }

    @Test
    @DisplayName("키워드 기반 식품 데이터 목록 검색 - 검색 결과가 없을 경우 빈 리스트 반환")
    void getMealDataListEmptyListTest() throws Exception {

        List<MealDataAddRequest> requests =
                List.of(
                        new MealDataAddRequest("통밀빵", 300, 100, 200, 40, 7, 3, IntakeUnit.G),
                        new MealDataAddRequest("통밀크래커", 300, 100, 200, 40, 7, 3, IntakeUnit.G),
                        new MealDataAddRequest("가지볶음", 300, 100, 200, 40, 7, 3, IntakeUnit.G));
        for (MealDataAddRequest request : requests) {
            addMealData(request);
        }

        JsonPath response =
                given().log()
                        .all()
                        .header(AUTHORIZATION, getAccessToken())
                        .param("ownerType", "ALL")
                        .param("page", 0)
                        .param("size", 20)
                        .when()
                        .get("api/v1/meal-data")
                        .then()
                        .log()
                        .all()
                        .statusCode(HttpStatus.OK.value())
                        .extract()
                        .jsonPath();

        assertAll(
                () -> assertThat(response.getInt("totalElements")).isEqualTo(0),
                () -> assertThat(response.getList("content")).isEmpty());
    }

    private void addMealData(MealDataAddRequest request) throws Exception {

        given().log()
                .all()
                .header(AUTHORIZATION, getAccessToken())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(toJson(request))
                .when()
                .post("/api/v1/meal-data")
                .then()
                .log()
                .all()
                .statusCode(HttpStatus.CREATED.value());
    }
}
