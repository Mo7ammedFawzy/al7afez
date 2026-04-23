package com.al7afez.al7afez.service;

import com.al7afez.al7afez.dto.LevelRequest;
import com.al7afez.al7afez.model.entities.Level;
import com.al7afez.al7afez.repositories.LevelRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class LevelService extends AbsMasterFileService<Level> {
    private final LevelRepository repository;
    private final MappingService mappingService;

    public LevelService(LevelRepository repository, MappingService mappingService) {
        this.repository = repository;
        this.mappingService = mappingService;
    }

    public Page<Level> getAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Level getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Level not found"));
    }

    public Level create(LevelRequest request) {
        Level level = new Level();
        mappingService.toLevel(level, request);
        return save(level, repository);
    }

    public Level update(Long id, LevelRequest request) {
        Level level = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Level not found"));
        mappingService.toLevel(level, request);
        return save(level, repository);
    }

    public void delete(Long id) {
        Level level = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Level not found"));
        isValidForDelete(level);
        repository.delete(level);
    }
}