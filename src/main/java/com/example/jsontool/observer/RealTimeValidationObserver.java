package com.example.jsontool.observer;

import com.example.jsontool.service.JsonEditorService;
import com.example.jsontool.service.ValidationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RealTimeValidationObserver implements Observer {

    private static final Logger log = LoggerFactory.getLogger(RealTimeValidationObserver.class);

    private final ValidationService validationService;

    public RealTimeValidationObserver(ValidationService validationService) {
        this.validationService = validationService;
    }

    @Override
    public void update(Subject subject) {
        if (subject instanceof JsonEditorService editor) {
            List<String> errors = validationService.validate(editor.getJsonContent(), editor.getSchemaContent());

            if (errors.isEmpty()) {
                log.info("[ValidationObserver]: Текст оновлено. Валідація успішна.");
            } else {
                log.info("[ValidationObserver]: Текст оновлено. Знайдено помилки: {}", errors);
            }
        }
    }
}
