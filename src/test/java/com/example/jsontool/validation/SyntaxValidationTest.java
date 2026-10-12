package com.example.jsontool.validation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

class SyntaxValidationTest {

    private final SyntaxValidation validation = new SyntaxValidation();

    @ParameterizedTest
    @ValueSource(strings = {"{}", "[]", "{\"a\": 1, \"b\": [true, null, \"x\"]}", "[1, -2.5e3]"})
    void acceptsValidJson(String json) {
        assertThat(validation.validate(json, null)).isEmpty();
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "{'a': 1}",          // одинарні лапки
            "{a: 1}",            // ключ без лапок
            "{\"a\": 1} trash",  // сміття після документа
            "{\"a\": 1,}",       // кома в кінці
            "{\"a\": ",          // незавершений документ
            "42"                 // не об'єкт і не масив
    })
    void rejectsInvalidJson(String json) {
        assertThat(validation.validate(json, null))
                .singleElement()
                .asString()
                .startsWith("Invalid JSON syntax");
    }

    @Test
    void rejectsEmptyInput() {
        assertThat(validation.validate("  ", null)).containsExactly("JSON is empty");
        assertThat(validation.validate(null, null)).containsExactly("JSON is empty");
    }
}
