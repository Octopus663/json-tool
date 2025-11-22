package com.example.jsontool.client;

import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import java.util.Base64;


public class JsonToolClient {

    private static final String API_URL = "http://localhost:8080/api/documents";

    public static void main(String[] args) {

        RestTemplate restTemplate = new RestTemplate();

        String plainCreds = "admin:password";
        byte[] plainCredsBytes = plainCreds.getBytes();
        String base64Creds = Base64.getEncoder().encodeToString(plainCredsBytes);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Basic " + base64Creds);

        HttpEntity<String> request = new HttpEntity<>(headers);

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