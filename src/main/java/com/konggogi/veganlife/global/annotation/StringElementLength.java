package com.konggogi.veganlife.global.annotation;


import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = ListStringValidator.class)
public @interface StringElementLength {
    int min() default 0;

    int max() default Integer.MAX_VALUE;

    String message() default "각 문자열은 길이가 {min}에서 {max} 사이여야 합니다.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
