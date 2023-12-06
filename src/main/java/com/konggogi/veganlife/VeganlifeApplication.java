package com.konggogi.veganlife;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class VeganlifeApplication {

    public static void main(String[] args) {
        SpringApplication.run(VeganlifeApplication.class, args);
    }
}
