package com.example.jsontool.service;

import com.example.jsontool.observer.Observer;
import com.example.jsontool.observer.Subject;
import jakarta.annotation.PostConstruct; // <-- НОВИЙ ІМПОРТ
import org.springframework.beans.factory.annotation.Autowired; // <-- НОВИЙ ІМПОРТ
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class JsonEditorService implements Subject {

    // Список підписників (спостерігачів)
    private final List<Observer> observers = new ArrayList<>();

    @Autowired
    private List<Observer> allRegisteredObservers;

    @PostConstruct
    public void registerObservers() {
        System.out.println("--- [Observer Pattern] Реєстрація спостерігачів... ---");
        for (Observer observer : allRegisteredObservers) {
            this.addObserver(observer);
            System.out.println("Зареєстровано: " + observer.getClass().getSimpleName());
        }
        System.out.println("--- Реєстрацію завершено ---");
    }

    private String jsonContent;
    private String schemaContent;

    @Override
    public void addObserver(Observer observer) {
        observers.add(observer);
    }

    @Override
    public void removeObserver(Observer observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers() {
        System.out.println("--- [Subject] Стан змінився! Сповіщую " + observers.size() + " спостерігачів... ---");
        for (Observer observer : observers) {
            observer.update(this);
        }
    }

    public void setTextContents(String jsonContent, String schemaContent) {
        this.jsonContent = jsonContent;
        this.schemaContent = schemaContent;

        notifyObservers();
    }

    // Геттери, щоб спостерігачі могли отримати новий стан
    public String getJsonContent() {
        return jsonContent;
    }

    public String getSchemaContent() {
        return schemaContent;
    }
}