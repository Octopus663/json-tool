package com.example.jsontool.service;

import com.example.jsontool.validation.SchemaValidation;
import com.example.jsontool.validation.SyntaxValidation;
import com.example.jsontool.validation.Validation;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Context патерну Strategy.
 */
@Service
public class ValidationService {

    private final Validation syntaxValidation = new SyntaxValidation();
    private final Validation schemaValidation = new SchemaValidation();

    public List<String> executeValidation(Validation strategy, String jsonText, String schemaText) {
        return strategy.validate(jsonText, schemaText);
    }

    /**
     * Обирає стратегію автоматично: якщо схема порожня — лише синтаксис, інакше — схема.
     */
    public List<String> validate(String jsonText, String schemaText) {
        Validation strategy = (schemaText == null || schemaText.isBlank()) ? syntaxValidation : schemaValidation;
        return executeValidation(strategy, jsonText, schemaText);
    }
}
