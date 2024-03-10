package com.konggogi.veganlife.member.service.dto;


import com.fasterxml.jackson.annotation.JsonIgnore;

public record IntakeCalorie(Integer breakfast, Integer lunch, Integer dinner, Integer snack) {
    @JsonIgnore
    public Integer getTotalCalorie() {
        return breakfast + lunch + dinner + snack;
    }
}
