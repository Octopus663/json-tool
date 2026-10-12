package com.example.jsontool.observer;

import com.example.jsontool.service.JsonEditorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class AutoSaveObserver implements Observer {

    private static final Logger log = LoggerFactory.getLogger(AutoSaveObserver.class);

    @Override
    public void update(Subject subject) {
        if (subject instanceof JsonEditorService editor) {
            String content = editor.getJsonContent();
            int length = content == null ? 0 : content.length();
            log.info("[AutoSaveObserver]: Зміст оновлено ({} символів). Виконую автозбереження...", length);
        }
    }
}
