package io.github.jonarzz.kata.unusual.spending.payment;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class CategoryTest {

    @ParameterizedTest
    @CsvSource(nullValues = "null", ignoreLeadingAndTrailingWhitespace = false, value = {"null", "''", "    "})
    void tryToCreateCategoryWithBlankName(String value) {
        assertThatThrownBy(() -> Category.named(value))
                .hasMessage("Category name cannot be blank");
    }
}