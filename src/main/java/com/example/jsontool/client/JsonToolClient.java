package com.example.jsontool.client;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

/**
 * Простий консольний клієнт для REST API.
 * Облікові дані беруться зі змінних оточення API_USERNAME / API_PASSWORD.
 */
public class JsonToolClient {

    private static final String API_URL = System.getenv().getOrDefault("API_URL", "http://localhost:8080/api/documents");

    public static void main(String[] args) {
        String username = System.getenv().getOrDefault("API_USERNAME", "admin");
        String password = System.getenv("API_PASSWORD");
        if (password == null || password.isBlank()) {
            System.err.println("Задайте змінну оточення API_PASSWORD");
            return;
        }

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(username, password);

        HttpEntity<Void> request = new HttpEntity<>(headers);

        try {
            System.out.println("--- КЛІЄНТ: Відправляю запит до Сервісу... ---");
            ResponseEntity<String> response = restTemplate.exchange(
                    API_URL,
                    HttpMethod.GET,
                    request,
                    String.class
            );

            System.out.println("--- СЕРВІС ВІДПОВІВ: ---");
            System.out.println("Статус: " + response.getStatusCode());
            System.out.println("Дані (JSON): " + response.getBody());

        } catch (Exception e) {
            System.out.println("Помилка доступу: " + e.getMessage());
        }
    }
}
