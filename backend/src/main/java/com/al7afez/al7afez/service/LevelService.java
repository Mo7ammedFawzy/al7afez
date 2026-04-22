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
public class LevelService {
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
        return repository.save(level);
    }

    public Level update(Long id, LevelRequest request) {
        Level level = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Level not found"));
        apply(level, request);
        return repository.save(level);
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Level not found");
        }
        repository.deleteById(id);
    }

    private void apply(Level level, LevelRequest request) {
        if (request == null || request.name() == null || request.name().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Level name is required");
        }
        level.setName(request.name().trim());
        level.setCode(normalize(request.code()));
        level.setFromSurah(request.fromSurah());
        level.setToSurah(request.toSurah());
        level.setFromAya(request.fromAya());
        level.setToAya(request.toAya());
        level.setNumberOfAyatPerSession(request.numberOfAyatPerSession());
    }

    private String normalize(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
