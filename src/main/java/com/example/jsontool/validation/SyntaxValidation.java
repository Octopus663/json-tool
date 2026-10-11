package com.example.jsontool.validation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Collections;
import java.util.List;

/**
 * Строга перевірка синтаксису JSON (Jackson): без одинарних лапок,
 * ключів без лапок та "сміття" після кінця документа.
 */
public class SyntaxValidation implements Validation {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
            .enable(DeserializationFeature.FAIL_ON_TRAILING_TOKENS);

    @Override
    public List<String> validate(String jsonText, String schemaText) {
        if (jsonText == null || jsonText.isBlank()) {
            return List.of("JSON is empty");
        }
        try {
            JsonNode node = OBJECT_MAPPER.readTree(jsonText);
            if (!node.isContainerNode()) {
                return List.of("Invalid JSON syntax: top-level value must be an object or an array");
            }
        } catch (JsonProcessingException e) {
            return List.of("Invalid JSON syntax: " + e.getOriginalMessage()
                    + locationOf(e));
        }
        return Collections.emptyList();
    }

    private static String locationOf(JsonProcessingException e) {
        if (e.getLocation() == null) {
            return "";
        }
        return " (line " + e.getLocation().getLineNr() + ", column " + e.getLocation().getColumnNr() + ")";
    }
}
