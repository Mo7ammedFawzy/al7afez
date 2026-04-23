package com.al7afez.al7afez.service;

import com.al7afez.al7afez.dto.SheikhRequest;
import com.al7afez.al7afez.model.entities.Sheikh;
import com.al7afez.al7afez.repositories.SheikhRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class SheikhService extends AbsMasterFileService<Sheikh> {
    private final SheikhRepository repository;
    private final MappingService mappingService;

    public SheikhService(SheikhRepository repository, MappingService mappingService) {
        this.repository = repository;
        this.mappingService = mappingService;
    }

    public Page<Sheikh> getAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Sheikh getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sheikh not found"));
    }

    public Sheikh create(SheikhRequest request) {
        Sheikh sheikh = new Sheikh();
        mappingService.toSheikh(sheikh, request);
        return save(sheikh, repository);
    }

    public Sheikh update(Long id, SheikhRequest request) {
        Sheikh sheikh = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sheikh not found"));
        mappingService.toSheikh(sheikh, request);
        return save(sheikh, repository);
    }

    public void delete(Long id) {
        Sheikh sheikh = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sheikh not found"));
        isValidForDelete(sheikh);
        repository.delete(sheikh);
    }
}