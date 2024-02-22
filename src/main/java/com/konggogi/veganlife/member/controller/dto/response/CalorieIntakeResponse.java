package com.konggogi.veganlife.member.controller.dto.response;


import com.konggogi.veganlife.member.service.dto.IntakeCalorie;
import java.util.List;

public record CalorieIntakeResponse(Integer totalCalorie, List<IntakeCalorie> periodicCalorie) {}
