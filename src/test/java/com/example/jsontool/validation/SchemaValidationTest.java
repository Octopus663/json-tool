package com.example.jsontool.validation;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SchemaValidationTest {

    private static final String SCHEMA = """
            {
              "type": "object",
              "properties": { "age": { "type": "integer", "minimum": 0 } },
              "required": ["age"]
            }
            """;

    private final SchemaValidation validation = new SchemaValidation();

    @Test
    void acceptsDocumentMatchingSchema() {
        assertThat(validation.validate("{\"age\": 30}", SCHEMA)).isEmpty();
    }

    @Test
    void reportsSchemaViolations() {
        assertThat(validation.validate("{\"age\": -1}", SCHEMA)).hasSize(1);
        assertThat(validation.validate("{}", SCHEMA)).singleElement().asString().contains("age");
    }

    @Test
    void reportsSyntaxErrorsBeforeSchema() {
        assertThat(validation.validate("{age: 1}", SCHEMA))
                .singleElement().asString().startsWith("Invalid JSON syntax");
    }

    @Test
    void reportsInvalidSchema() {
        assertThat(validation.validate("{\"age\": 1}", "{not a schema"))
                .singleElement().asString().startsWith("Validation failed");
    }
}
