package com.patrimoine.rattrapage.backend.controller;

import com.patrimoine.rattrapage.backend.model.Patrimoine;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/patrimoines")
public class PatrimoineController {

    private static final Map<String, Patrimoine> patrimoineStore = new HashMap<>();
    private static final Path filePath = Paths.get("patrimoines.txt");

    @PutMapping("/{id}")
    public Patrimoine createOrUpdatePatrimoine(@PathVariable String id, @RequestBody Patrimoine patrimoine) throws IOException {
        patrimoine.setDerniereModification(LocalDateTime.now());
        patrimoineStore.put(id, patrimoine);
        saveToFile();
        return patrimoine;
    }

    @GetMapping("/{id}")
    public Patrimoine getPatrimoine(@PathVariable String id) {
        Patrimoine patrimoine = patrimoineStore.get(id);
        if (patrimoine == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND); // Ajout de cette ligne
        }
        return patrimoine;
    }

    private void saveToFile() throws IOException {
        StringBuilder data = new StringBuilder();
        for (Map.Entry<String, Patrimoine> entry : patrimoineStore.entrySet()) {
            data.append(entry.getKey()).append(",")
                    .append(entry.getValue().getPossesseur()).append(",")
                    .append(entry.getValue().getDerniereModification()).append("\n");
        }
        Files.write(filePath, data.toString().getBytes());
    }


    public void setPatrimoineStore(Map<String, Patrimoine> patrimoineStore) {
        PatrimoineController.patrimoineStore.clear();
        PatrimoineController.patrimoineStore.putAll(patrimoineStore);
    }
}