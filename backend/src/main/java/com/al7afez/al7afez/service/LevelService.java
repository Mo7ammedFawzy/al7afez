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

    public LevelService(LevelRepository repository) {
        this.repository = repository;
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
        apply(level, request);
        return save(level, repository);
    }

    public Level update(Long id, LevelRequest request) {
        Level level = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Level not found"));
        apply(level, request);
        return save(level, repository);
    }

    public void delete(Long id) {
        Level level = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Level not found"));
        isValidForDelete(level);
        repository.delete(level);
    }

    private void apply(Level level, LevelRequest request) {
        level.setName(request.name().trim());
        level.setCode(normalize(request.code()));
        level.setFromSurah(request.fromSurah());
        level.setToSurah(request.toSurah());
        level.setFromAya(request.fromAya());
        level.setToAya(request.toAya());
        level.setNumberOfAyatPerSession(request.numberOfAyatPerSession());
    }
}