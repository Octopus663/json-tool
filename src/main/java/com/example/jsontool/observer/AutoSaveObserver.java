package com.example.jsontool.observer;

import com.example.jsontool.service.JsonEditorService;
import org.springframework.stereotype.Component;

@Component
public class AutoSaveObserver implements Observer {

    // @Autowired
    // private HistoryService historyService;

    @Override
    public void update(Subject subject) {
        if (subject instanceof JsonEditorService) {
            JsonEditorService editor = (JsonEditorService) subject;
            String contentToSave = editor.getJsonContent();

            // historyService.saveHistory(contentToSave);

            System.out.println("[AutoSaveObserver]: Зміст оновлено! Виконую автозбереження...");
        }
    }
}