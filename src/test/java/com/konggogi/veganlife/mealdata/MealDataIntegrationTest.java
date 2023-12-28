package com.konggogi.veganlife.mealdata;

import static io.restassured.RestAssured.given;

import com.konggogi.veganlife.mealdata.controller.dto.request.MealDataAddRequest;
import com.konggogi.veganlife.mealdata.domain.IntakeUnit;
import com.konggogi.veganlife.mealdata.domain.mapper.MealDataMapper;
import com.konggogi.veganlife.mealdata.service.MealDataQueryService;
import com.konggogi.veganlife.mealdata.service.MealDataService;
import com.konggogi.veganlife.support.restassured.IntegrationTest;
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
    void getMealDataListTest() throws Exception {

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
}
