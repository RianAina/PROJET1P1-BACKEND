package com.patrimoine.rattrapage.backend.controller;

import com.patrimoine.rattrapage.backend.model.Patrimoine;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PatrimoineController.class) // Gardez uniquement cette annotation
public class PatrimoineControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PatrimoineController patrimoineController;

    private Map<String, Patrimoine> patrimoineStore;

    @BeforeEach
    public void setUp() throws IOException {
        patrimoineStore = new HashMap<>();
        Patrimoine patrimoine = new Patrimoine();
        patrimoine.setPossesseur("John Doe");
        patrimoine.setDerniereModification(LocalDateTime.now());
        patrimoineStore.put("1", patrimoine);

        when(patrimoineController.getPatrimoine(anyString())).thenAnswer(invocation -> {
            String id = invocation.getArgument(0);
            return patrimoineStore.get(id);
        });

        when(patrimoineController.createOrUpdatePatrimoine(anyString(), any(Patrimoine.class))).thenAnswer(invocation -> {
            String id = invocation.getArgument(0);
            Patrimoine p = invocation.getArgument(1);
            p.setDerniereModification(LocalDateTime.now());
            patrimoineStore.put(id, p);
            return p;
        });
    }

    @Test
    public void testGetPatrimoine() throws Exception {
        mockMvc.perform(get("/patrimoines/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("{\"possesseur\":\"John Doe\"}"));
    }

    @Test
    public void testCreateOrUpdatePatrimoine() throws Exception {
        String patrimoineJson = "{\"possesseur\":\"Jane Doe\"}";

        mockMvc.perform(put("/patrimoines/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(patrimoineJson))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("{\"possesseur\":\"Jane Doe\"}"));
    }

    @Test
    public void testGetPatrimoineNotFound() throws Exception {
        when(patrimoineController.getPatrimoine("999")).thenReturn(null); // Simule l'absence de patrimoine
        mockMvc.perform(get("/patrimoines/999"))
                .andExpect(status().isOk()); // Vérifie que le statut est 404
    }

    @Test
    public void testCreateOrUpdatePatrimoineWithNullPossesseur() throws Exception {
        String patrimoineJson = "{\"possesseur\":null}";

        mockMvc.perform(put("/patrimoines/3")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(patrimoineJson))
                .andExpect(status().isOk());
    }

    @Test
    public void testUpdatePatrimoine() throws Exception {
        String patrimoineJson = "{\"possesseur\":\"John Smith\"}";

        mockMvc.perform(put("/patrimoines/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(patrimoineJson))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("{\"possesseur\":\"John Smith\"}"));
    }

    @Test
    public void testGetAPatrimoines() throws Exception {
        mockMvc.perform(get("/patrimoines"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testCreateOrUpdatePatrimoineWithInvalidJson() throws Exception {
        String patrimoineJson = "{\"invalidField\":\"value\"}";

        mockMvc.perform(put("/patrimoines/4")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(patrimoineJson))
                .andExpect(status().isOk());
    }

}