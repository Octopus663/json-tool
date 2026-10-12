package com.example.jsontool.dto;

import com.example.jsontool.model.JSONDocument;

import java.time.LocalDateTime;

/**
 * Представлення документа для REST API (без внутрішніх зв'язків сутності, напр. User).
 */
public record DocumentDto(Long id, String name, String content, LocalDateTime createdAt, LocalDateTime modifiedAt) {

    public static DocumentDto from(JSONDocument document) {
        return new DocumentDto(document.getId(), document.getName(), document.getContent(),
                document.getCreatedAt(), document.getModifiedAt());
    }
}
