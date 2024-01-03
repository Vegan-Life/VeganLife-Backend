package com.konggogi.veganlife.support.utils;


import java.time.LocalDate;
import java.time.temporal.TemporalAccessor;
import java.util.Optional;
import org.springframework.data.auditing.DateTimeProvider;
import org.springframework.stereotype.Component;

@Component
public class DateTimeImpl implements DateTimeProvider {

    public static LocalDate now = LocalDate.of(2023, 12, 23);

    @Override
    public Optional<TemporalAccessor> getNow() {
        return Optional.of(LocalDate.of(2023, 12, 23));
    }
}
