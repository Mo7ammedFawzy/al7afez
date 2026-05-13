package com.al7afez.al7afez.controllers;

import com.al7afez.al7afez.dto.RecitationRequest;
import com.al7afez.al7afez.dto.RecitationResponse;
import com.al7afez.al7afez.dto.RecitationSuggestionResponse;
import com.al7afez.al7afez.service.RecitationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/recitations")
public class RecitationDocumentController {
    private final RecitationService service;

    public RecitationDocumentController(RecitationService service) {
        this.service = service;
    }

    @GetMapping
    public Page<RecitationResponse> getAll(
            @RequestParam(required = false) Long studentId,
            @PageableDefault(size = 10) Pageable pageable
    ) {
        if (studentId != null) {
            return service.getByStudentId(studentId, pageable);
        }
        return service.getAll(pageable);
    }

    @GetMapping("/suggest")
    public ResponseEntity<RecitationSuggestionResponse> suggest(@RequestParam Long studentId) {
        return ResponseEntity.ok(service.suggestNext(studentId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RecitationResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PostMapping
    public ResponseEntity<RecitationResponse> create(@RequestBody RecitationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RecitationResponse> update(@PathVariable Long id, @RequestBody RecitationRequest request) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
