package com.example.jsontool.controller;

import com.example.jsontool.command.Command;
import com.example.jsontool.command.CommandManager;
import com.example.jsontool.command.CreateDocumentCommand;
import com.example.jsontool.model.JSONDocument;
import com.example.jsontool.service.DocumentService;
import com.example.jsontool.service.JsonEditorService;
import com.example.jsontool.service.SyntaxHighlighterService;
import com.example.jsontool.service.ValidationService;
import com.example.jsontool.template.DocumentExporter;
import com.example.jsontool.template.MarkdownExporter;
import com.example.jsontool.template.TxtExporter;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.HtmlUtils;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/documents")
public class DocumentController {

    private final DocumentService documentService;
    private final ValidationService validationService;
    private final JsonEditorService jsonEditorService;
    private final CommandManager commandManager;
    private final MarkdownExporter markdownExporter;
    private final TxtExporter txtExporter;
    private final SyntaxHighlighterService highlighterService;

    public DocumentController(DocumentService documentService,
                              ValidationService validationService,
                              JsonEditorService jsonEditorService,
                              CommandManager commandManager,
                              MarkdownExporter markdownExporter,
                              TxtExporter txtExporter,
                              SyntaxHighlighterService highlighterService) {
        this.documentService = documentService;
        this.validationService = validationService;
        this.jsonEditorService = jsonEditorService;
        this.commandManager = commandManager;
        this.markdownExporter = markdownExporter;
        this.txtExporter = txtExporter;
        this.highlighterService = highlighterService;
    }

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
    public String saveDocument(@ModelAttribute("document") JSONDocument form, Model model,
                               RedirectAttributes redirectAttributes) {
        // Створюємо новий об'єкт, щоб клієнт не міг підмінити id/user і перезаписати чужий документ
        JSONDocument document = new JSONDocument(form.getName(), form.getContent(), null);
        Command createCommand = new CreateDocumentCommand(documentService, document);

        try {
            commandManager.executeCommand(createCommand);
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "create-document";
        }

        redirectAttributes.addFlashAttribute("message", "Документ \"" + document.getName() + "\" збережено");
        return "redirect:/documents";
    }

    @PostMapping("/undo")
    public String undoSave(RedirectAttributes redirectAttributes) {
        boolean undone = commandManager.undoLastCommand();
        redirectAttributes.addFlashAttribute("message",
                undone ? "Останню дію скасовано" : "Немає дій для скасування");
        return "redirect:/documents";
    }

    @PostMapping("/validate")
    @ResponseBody
    public Map<String, List<String>> validateDocument(
            @RequestParam("jsonText") String jsonText,
            @RequestParam(value = "schemaText", required = false) String schemaText) {

        jsonEditorService.setTextContents(jsonText, schemaText);

        return Map.of("errors", validationService.validate(jsonText, schemaText));
    }

    @GetMapping("/{id}/export/markdown")
    public ResponseEntity<String> exportToMarkdown(@PathVariable Long id) {
        return export(findDocument(id), markdownExporter, ".md", MediaType.TEXT_MARKDOWN);
    }

    @GetMapping("/{id}/export/txt")
    public ResponseEntity<String> exportToTxt(@PathVariable Long id) {
        return export(findDocument(id), txtExporter, ".txt", MediaType.TEXT_PLAIN);
    }

    @GetMapping("/highlight-demo")
    @ResponseBody
    public String demoFlyweight() {
        String json = "{\"id\": 1, \"name\": \"Test\", \"isActive\": true, \"count\": 100, \"role\": \"ADMIN\"}";

        return "<html><head><meta charset='UTF-8'></head><body><h1>Flyweight Demo</h1>" +
                "<p>Оригінал: " + HtmlUtils.htmlEscape(json) + "</p>" +
                "<h3>Підсвічений JSON (HTML generated by Flyweights):</h3>" +
                "<pre style='font-family: monospace; background: #f0f0f0; padding: 10px;'>" +
                highlighterService.highlightJson(json) +
                "</pre></body></html>";
    }

    @GetMapping("/{id}")
    public String viewDocument(@PathVariable Long id, Model model) {
        JSONDocument document = findDocument(id);

        model.addAttribute("document", document);
        model.addAttribute("highlightedContent", highlighterService.highlightJson(document.getContent()));

        return "view-document";
    }

    private JSONDocument findDocument(Long id) {
        return documentService.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Документ не знайдено: " + id));
    }

    private ResponseEntity<String> export(JSONDocument document, DocumentExporter exporter,
                                          String extension, MediaType mediaType) {
        ContentDisposition disposition = ContentDisposition.attachment()
                .filename(document.getName() + extension, StandardCharsets.UTF_8)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, disposition.toString())
                .contentType(new MediaType(mediaType, StandardCharsets.UTF_8))
                .body(exporter.exportDocument(document));
    }
}
