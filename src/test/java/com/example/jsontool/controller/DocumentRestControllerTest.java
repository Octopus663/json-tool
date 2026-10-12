package com.example.jsontool.controller;

import com.example.jsontool.model.JSONDocument;
import com.example.jsontool.repository.JSONDocumentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class DocumentRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JSONDocumentRepository repository;

    @BeforeEach
    void cleanDatabase() {
        repository.deleteAll();
    }

    @Test
    void requiresAuthentication() throws Exception {
        mockMvc.perform(get("/api/documents"))
                .andExpect(status().isUnauthorized());
        mockMvc.perform(get("/api/documents").with(httpBasic("testuser", "wrong")))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void listsDocumentsWithoutInternalFields() throws Exception {
        repository.save(new JSONDocument("one", "{}", null));

        mockMvc.perform(get("/api/documents").with(httpBasic("testuser", "testpass")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("one"))
                .andExpect(jsonPath("$[0].user").doesNotExist());
    }

    @Test
    void createsDocument() throws Exception {
        mockMvc.perform(post("/api/documents").with(httpBasic("testuser", "testpass"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"api-doc\", \"content\": \"[1, 2]\"}"))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", containsString("/api/documents/")))
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value("api-doc"));
    }

    @Test
    void rejectsInvalidDocument() throws Exception {
        mockMvc.perform(post("/api/documents").with(httpBasic("testuser", "testpass"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"bad\", \"content\": \"{oops\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value(containsString("Invalid JSON syntax")));
    }

    @Test
    void unknownDocumentReturns404() throws Exception {
        mockMvc.perform(get("/api/documents/{id}", 999_999).with(httpBasic("testuser", "testpass")))
                .andExpect(status().isNotFound());
    }
}
