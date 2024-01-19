package com.konggogi.veganlife.global.util;


import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import org.springframework.util.Assert;

public class RandomUtils {

    public static long getSeedWithLocalDate(long init, LocalDate date) {

        return init + date.toEpochDay();
    }

    public static List<Integer> generateNotDuplicatedRandomNumbers(int bound, int size, long seed) {

        Assert.isTrue(size > 0, "size는 양수여야 합니다.");

        Random random = new Random(seed);
        Set<Integer> generated = new HashSet<>();

        do {
            generated.add(random.nextInt(bound));
        } while (generated.size() < bound && generated.size() < size);

        return generated.stream().toList();
    }
}
