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
public class SheikhService {
    private final SheikhRepository repository;

    public SheikhService(SheikhRepository repository) {
        this.repository = repository;
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
        apply(sheikh, request);
        return repository.save(sheikh);
    }

    public Sheikh update(Long id, SheikhRequest request) {
        Sheikh sheikh = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sheikh not found"));
        apply(sheikh, request);
        return repository.save(sheikh);
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Sheikh not found");
        }
        repository.deleteById(id);
    }

    private void apply(Sheikh sheikh, SheikhRequest request) {
        if (request == null || request.name() == null || request.name().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Sheikh name is required");
        }
        sheikh.setName(request.name().trim());
        sheikh.setCode(normalize(request.code()));
        sheikh.setBirthDate(request.birthDate());
        sheikh.setPhoneNumber(normalize(request.phoneNumber()));
        sheikh.setGender(request.gender());
    }

    private String normalize(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
