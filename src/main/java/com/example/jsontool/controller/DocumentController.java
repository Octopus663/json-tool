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
import com.example.jsontool.template.MarkdownExporter;
import com.example.jsontool.template.TxtExporter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;

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

    @Autowired
    private MarkdownExporter markdownExporter;

    @Autowired
    private TxtExporter txtExporter;

    @Autowired
    private com.example.jsontool.service.SyntaxHighlighterService highlighterService;

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

    @GetMapping("/{id}/export/markdown")
    public ResponseEntity<String> exportToMarkdown(@PathVariable Long id) {
        JSONDocument document = documentService.findAll().stream()
                .filter(d -> d.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid document Id:" + id));

        String markdownContent = markdownExporter.exportDocument(document);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + document.getName() + ".md\"")
                .contentType(MediaType.TEXT_MARKDOWN)
                .body(markdownContent);
    }

    @GetMapping("/{id}/export/txt")
    public ResponseEntity<String> exportToTxt(@PathVariable Long id) {
        JSONDocument document = documentService.findAll().stream()
                .filter(d -> d.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid document Id:" + id));

        String txtContent = txtExporter.exportDocument(document);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + document.getName() + ".txt\"")
                .contentType(MediaType.TEXT_PLAIN)
                .body(txtContent);
    }

    @GetMapping("/highlight-demo")
    @ResponseBody
    public String demoFlyweight() {
        String json = "{\"id\": 1, \"name\": \"Test\", \"isActive\": true, \"count\": 100, \"role\": \"ADMIN\"}";

        return "<html><body><h1>Flyweight Demo</h1>" +
                "<p>Оригінал: " + json + "</p>" +
                "<h3>Підсвічений JSON (HTML generated by Flyweights):</h3>" +
                "<div style='font-family: monospace; background: #f0f0f0; padding: 10px;'>" +
                highlighterService.highlightJson(json) +
                "</div></body></html>";
    }

    @GetMapping("/{id}")
    public String viewDocument(@PathVariable Long id, Model model) {

        JSONDocument document = documentService.findAll().stream()
                .filter(d -> d.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid document Id:" + id));

        String highlightedContent = highlighterService.highlightJson(document.getContent());

        model.addAttribute("document", document);
        model.addAttribute("highlightedContent", highlightedContent);

        return "view-document";
    }
}