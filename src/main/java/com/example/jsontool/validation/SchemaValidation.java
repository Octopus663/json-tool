package com.example.jsontool.validation;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;

import java.util.List;
import java.util.Set;

public class SchemaValidation implements Validation {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final JsonSchemaFactory FACTORY = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V7);

    @Override
    public List<String> validate(String jsonText, String schemaText) {
        List<String> syntaxErrors = new SyntaxValidation().validate(jsonText, null);
        if (!syntaxErrors.isEmpty()) {
            return syntaxErrors;
        }
        try {
            JsonNode schemaNode = OBJECT_MAPPER.readTree(schemaText);
            JsonSchema schema = FACTORY.getSchema(schemaNode);

            JsonNode jsonNode = OBJECT_MAPPER.readTree(jsonText);
            Set<ValidationMessage> errors = schema.validate(jsonNode);
            return errors.stream()
                    .map(ValidationMessage::getMessage)
                    .toList();

        } catch (Exception e) {
            return List.of("Validation failed due to an error: " + e.getMessage());
        }
    }
}
