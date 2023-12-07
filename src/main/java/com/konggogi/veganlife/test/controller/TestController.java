package com.konggogi.veganlife.test.controller;


import com.konggogi.veganlife.test.controller.dto.request.TestRequest;
import com.konggogi.veganlife.test.controller.dto.response.TestResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** RestDocs를 설명하기 위한 TestController입니다. */
@Slf4j
@RestController
public class TestController {

    @GetMapping("api/v1/test/{path}")
    public ResponseEntity<TestResponse> getTest(
            @PathVariable String path, @RequestParam String query) {

        log.info("Path Variable: {}", path);
        log.info("Query Parameter: {}", query);
        return ResponseEntity.ok(TestResponse.from(path + query));
    }

    @PostMapping("api/v1/test")
    public ResponseEntity<TestResponse> postTest(@RequestBody TestRequest request) {

        log.info("Request Body: {}", request);
        return ResponseEntity.ok(TestResponse.from(request.message()));
    }
}
