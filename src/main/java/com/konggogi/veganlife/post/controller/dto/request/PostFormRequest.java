package com.konggogi.veganlife.post.controller.dto.request;


import com.konggogi.veganlife.global.annotation.StringElementLength;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;
import org.hibernate.validator.constraints.Length;

public record PostFormRequest(
        @NotBlank @Length(min = 1, max = 20) String title,
        @NotBlank @Length(min = 1, max = 1000) String content,
        @Size(max = 5) @StringElementLength(min = 2, max = 10) List<String> tags) {}
