package com.al7afez.al7afez.controllers;

import com.al7afez.al7afez.entities.Sheikh;
import com.al7afez.al7afez.repositories.SheikhRepository;
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
@RequestMapping("/api/sheikhs")
public class SheikhController {
    private final SheikhRepository repository;

    public SheikhController(SheikhRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public Page<Sheikh> getAll(@PageableDefault(size = 10) Pageable pageable) {
        return repository.findAll(pageable);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Sheikh> getById(@PathVariable Long id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Sheikh> create(@RequestBody Sheikh sheikh) {
        Sheikh saved = repository.save(sheikh);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Sheikh> update(@PathVariable Long id, @RequestBody Sheikh sheikh) {
        if (!repository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        sheikh.setId(id);
        Sheikh saved = repository.save(sheikh);
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
