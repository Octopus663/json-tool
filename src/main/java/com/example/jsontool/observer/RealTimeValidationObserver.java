package com.example.jsontool.observer;

import com.example.jsontool.service.JsonEditorService;
import com.example.jsontool.service.ValidationService;
import com.example.jsontool.validation.SchemaValidation;
import com.example.jsontool.validation.SyntaxValidation;
import com.example.jsontool.validation.Validation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RealTimeValidationObserver implements Observer {

    @Autowired
    private ValidationService validationService;

    @Override
    public void update(Subject subject) {
        if (subject instanceof JsonEditorService) {
            JsonEditorService editor = (JsonEditorService) subject;
            String json = editor.getJsonContent();
            String schema = editor.getSchemaContent();

            Validation strategy = (schema == null || schema.trim().isEmpty())
                    ? new SyntaxValidation()
                    : new SchemaValidation();

            List<String> errors = validationService.executeValidation(strategy, json, schema);

            if (errors.isEmpty()) {
                System.out.println("[ValidationObserver]: Текст оновлено. Валідація успішна.");
            } else {
                System.out.println("[ValidationObserver]: Текст оновлено. Знайдено помилки: " + errors);
            }
        }
    }
}