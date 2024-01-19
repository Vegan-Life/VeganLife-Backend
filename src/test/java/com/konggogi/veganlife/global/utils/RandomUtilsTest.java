package com.konggogi.veganlife.global.utils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.konggogi.veganlife.global.util.RandomUtils;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class RandomUtilsTest {

    @Test
    @DisplayName("같은 seed일 경우 항상 같은 랜덤 숫자들을 반환한다.")
    void generateNotDuplicatedRandomNumbersAreAlwaysSameTest() {

        List<Integer> result1 = RandomUtils.generateNotDuplicatedRandomNumbers(100, 3, 100L);
        List<Integer> result2 = RandomUtils.generateNotDuplicatedRandomNumbers(100, 3, 100L);

        assertThat(result1).hasSize(3);
        assertThat(result2).hasSize(3);
        assertThat(result1.get(0)).isEqualTo(result2.get(0));
        assertThat(result1.get(1)).isEqualTo(result2.get(1));
        assertThat(result1.get(2)).isEqualTo(result2.get(2));
    }

    @Test
    @DisplayName("뽑을 수 있는 숫자 범위인 bound가 뽑고자 하는 size보다 작은 경우, bound개를 뽑았다면 바로 결과를 반환한다.")
    void generateNotDuplicatedRandomNumbersEarlyReturnTest() {

        List<Integer> result = RandomUtils.generateNotDuplicatedRandomNumbers(1, 100, 100L);

        assertThat(result).hasSize(1);
    }

    @Test
    @DisplayName("뽑을 숫자의 개수는 양수여야 한다.")
    void generateNotDuplicatedRandomNumbersSizeIsPositiveTest() {

        assertThatThrownBy(() -> RandomUtils.generateNotDuplicatedRandomNumbers(1, 0, 100L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("size는 양수여야 합니다.");
    }
}
