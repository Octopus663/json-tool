package com.example.jsontool.controller;

import com.example.jsontool.command.Command;
import com.example.jsontool.model.JSONDocument;
import com.example.jsontool.service.DocumentService;
import com.example.jsontool.service.ValidationService;
import com.example.jsontool.validation.SchemaValidation;
import com.example.jsontool.validation.SyntaxValidation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import java.util.List;
import java.util.Map;
import com.example.jsontool.command.CommandManager;
import com.example.jsontool.command.CreateDocumentCommand;
import com.example.jsontool.service.JsonEditorService;

@Controller
@RequestMapping("/documents")
public class DocumentController {

    @Autowired
    private DocumentService documentService;

    @Autowired
    private ValidationService validationService;

    @Autowired
    private JsonEditorService jsonEditorService;

    @Autowired
    private CommandManager commandManager;

    @GetMapping
    public String listDocuments(Model model) {
        model.addAttribute("documents", documentService.findAll());
        return "documents";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("document", new JSONDocument());
        return "create-document";
    }

    @PostMapping("/save")
    public String saveDocument(JSONDocument document) {
        Command createCommand = new CreateDocumentCommand(documentService, document);

        // 3. І доручаємо "Виконавцю" (Invoker) її виконати
        commandManager.executeCommand(createCommand);

        return "redirect:/documents"; // Повернення на список після збереження
    }

    @GetMapping("/undo")
    public String undoSave() {
        commandManager.undoLastCommand();
        return "redirect:/documents";
    }

    @PostMapping("/validate")
    @ResponseBody
    public Map<String, List<String>> validateDocument(
            @RequestParam("jsonText") String jsonText,
            @RequestParam("schemaText") String schemaText) {

        jsonEditorService.setTextContents(jsonText, schemaText);

        List<String> errors;
        if (schemaText == null || schemaText.trim().isEmpty()) {
            errors = validationService.executeValidation(new SyntaxValidation(), jsonText, null);
        } else {
            errors = validationService.executeValidation(new SchemaValidation(), jsonText, schemaText);
        }

        return Map.of("errors", errors);
    }
}