package com.example.jsontool.service;

import com.example.jsontool.observer.Observer;
import com.example.jsontool.observer.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Subject патерну Observer. Бін — singleton, тому зміна стану та сповіщення
 * виконуються атомарно (synchronized), щоб спостерігачі бачили узгоджений стан.
 */
@Service
public class JsonEditorService implements Subject {

    private static final Logger log = LoggerFactory.getLogger(JsonEditorService.class);

    private final List<Observer> observers = new CopyOnWriteArrayList<>();

    private String jsonContent;
    private String schemaContent;

    public JsonEditorService(List<Observer> registeredObservers) {
        log.info("[Observer Pattern] Реєстрація спостерігачів...");
        for (Observer observer : registeredObservers) {
            addObserver(observer);
            log.info("Зареєстровано: {}", observer.getClass().getSimpleName());
        }
    }

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
        log.debug("[Subject] Стан змінився! Сповіщую {} спостерігачів...", observers.size());
        for (Observer observer : observers) {
            observer.update(this);
        }
    }

    public synchronized void setTextContents(String jsonContent, String schemaContent) {
        this.jsonContent = jsonContent;
        this.schemaContent = schemaContent;

        notifyObservers();
    }

    public synchronized String getJsonContent() {
        return jsonContent;
    }

    public synchronized String getSchemaContent() {
        return schemaContent;
    }
}
