package com.konggogi.veganlife.restdocs.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/v1/docs")
public class RestDocsController {

    @GetMapping
    public String getRestDocs() {

        return "index";
    }
}
