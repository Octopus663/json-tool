package com.example.jsontool.flyweight;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class JsonStyleFactoryTest {

    @Test
    void returnsSameInstanceForSameType() {
        assertThat(JsonStyleFactory.getStyle("KEY")).isSameAs(JsonStyleFactory.getStyle("KEY"));
        assertThat(JsonStyleFactory.getStyle("KEY")).isNotSameAs(JsonStyleFactory.getStyle("STRING"));
    }

    @Test
    void unknownTypeFallsBackToDefaultStyle() {
        assertThat(JsonStyleFactory.getStyle("SOMETHING").getColorHex()).isEqualTo("#000000");
    }
}
