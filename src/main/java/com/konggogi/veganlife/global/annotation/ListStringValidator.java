package com.konggogi.veganlife.global.annotation;


import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.List;

public class ListStringValidator implements ConstraintValidator<StringElementLength, List<String>> {
    private int min;
    private int max;

    @Override
    public void initialize(StringElementLength constraintAnnotation) {
        this.min = constraintAnnotation.min();
        this.max = constraintAnnotation.max();
    }

    @Override
    public boolean isValid(List<String> values, ConstraintValidatorContext context) {
        if (values == null) {
            return false;
        }
        return values.stream().allMatch(value -> min <= value.length() && value.length() <= max);
    }
}
