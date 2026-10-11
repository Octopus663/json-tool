package com.example.jsontool.controller;

import com.example.jsontool.model.JSONDocument;
import com.example.jsontool.repository.JSONDocumentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class DocumentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JSONDocumentRepository repository;

    @BeforeEach
    void cleanDatabase() {
        repository.deleteAll();
    }

    @Test
    void homePageRenders() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("JSON Tool")));
    }

    @Test
    void createFormRendersWithCsrfToken() throws Exception {
        mockMvc.perform(get("/documents/new"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("name=\"_csrf\"")));
    }

    @Test
    void savesValidDocumentAndShowsItInList() throws Exception {
        mockMvc.perform(post("/documents/save").with(csrf())
                        .param("name", "config")
                        .param("content", "{\"a\": 1}"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/documents"));

        assertThat(repository.findAll()).singleElement()
                .extracting(JSONDocument::getName).isEqualTo("config");

        mockMvc.perform(get("/documents"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("config")));
    }

    @Test
    void rejectsInvalidJsonAndShowsError() throws Exception {
        mockMvc.perform(post("/documents/save").with(csrf())
                        .param("name", "broken")
                        .param("content", "{a: 1}"))
                .andExpect(status().isOk())
                .andExpect(view().name("create-document"))
                .andExpect(model().attributeExists("error"));

        assertThat(repository.count()).isZero();
    }

    @Test
    void saveCannotOverwriteExistingDocumentById() throws Exception {
        JSONDocument existing = repository.save(new JSONDocument("original", "{}", null));

        mockMvc.perform(post("/documents/save").with(csrf())
                        .param("id", existing.getId().toString())
                        .param("name", "hacked")
                        .param("content", "[]"))
                .andExpect(status().is3xxRedirection());

        assertThat(repository.findById(existing.getId())).get()
                .extracting(JSONDocument::getName).isEqualTo("original");
        assertThat(repository.count()).isEqualTo(2);
    }

    @Test
    void saveWithoutCsrfTokenIsForbidden() throws Exception {
        mockMvc.perform(post("/documents/save")
                        .param("name", "config")
                        .param("content", "{}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void undoRemovesLastSavedDocument() throws Exception {
        mockMvc.perform(post("/documents/save").with(csrf())
                .param("name", "temp")
                .param("content", "{}"));
        assertThat(repository.count()).isEqualTo(1);

        mockMvc.perform(post("/documents/undo").with(csrf()))
                .andExpect(status().is3xxRedirection());

        assertThat(repository.count()).isZero();
    }

    @Test
    void viewEscapesUserContent() throws Exception {
        JSONDocument doc = repository.save(new JSONDocument("xss", "{\"a\": \"<script>alert(1)</script>\"}", null));

        mockMvc.perform(get("/documents/{id}", doc.getId()))
                .andExpect(status().isOk())
                .andExpect(content().string(not(containsString("<script>alert(1)</script>"))))
                .andExpect(content().string(containsString("&lt;script&gt;")));
    }

    @Test
    void unknownDocumentReturns404() throws Exception {
        mockMvc.perform(get("/documents/{id}", 999_999))
                .andExpect(status().isNotFound());
        mockMvc.perform(get("/documents/{id}/export/txt", 999_999))
                .andExpect(status().isNotFound());
    }

    @Test
    void exportUsesSafeContentDisposition() throws Exception {
        JSONDocument doc = repository.save(new JSONDocument("звіт \"1\"", "{}", null));

        mockMvc.perform(get("/documents/{id}/export/markdown", doc.getId()))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", containsString("filename*=UTF-8''")))
                .andExpect(content().string(containsString("```json")));
    }

    @Test
    void validateEndpointReturnsErrors() throws Exception {
        mockMvc.perform(post("/documents/validate").with(csrf())
                        .param("jsonText", "{a: 1}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.errors[0]").value(containsString("Invalid JSON syntax")));

        mockMvc.perform(post("/documents/validate").with(csrf())
                        .param("jsonText", "{\"a\": 1}")
                        .param("schemaText", "{\"type\": \"array\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.errors.length()").value(1));
    }
}
