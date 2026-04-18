package com.al7afez.al7afez.controllers;

import com.al7afez.al7afez.entities.MistakeType;
import com.al7afez.al7afez.repositories.MistakeTypeRepository;
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
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/mistake-types")
public class MistakeTypeController {
    private final MistakeTypeRepository repository;

    public MistakeTypeController(MistakeTypeRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public Page<MistakeType> getAll(@PageableDefault(size = 10) Pageable pageable) {
        return repository.findAll(pageable);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MistakeType> getById(@PathVariable Long id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<MistakeType> create(@RequestBody MistakeType mistakeType) {
        MistakeType saved = repository.save(mistakeType);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MistakeType> update(@PathVariable Long id, @RequestBody MistakeType mistakeType) {
        if (!repository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        mistakeType.setId(id);
        MistakeType saved = repository.save(mistakeType);
        return ResponseEntity.ok(saved);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!repository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
