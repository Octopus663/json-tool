package com.example.jsontool.dto;

/**
 * Тіло запиту на створення документа через REST API.
 */
public record DocumentRequest(String name, String content) {
}
