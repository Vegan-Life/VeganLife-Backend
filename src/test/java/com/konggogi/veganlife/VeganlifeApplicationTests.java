package com.konggogi.veganlife;


import com.konggogi.veganlife.support.restassured.IntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class VeganlifeApplicationTests extends IntegrationTest {
    @Test
    void contextLoads() {}
}
