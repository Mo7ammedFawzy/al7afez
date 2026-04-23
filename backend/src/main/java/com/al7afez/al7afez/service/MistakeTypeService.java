package com.al7afez.al7afez.service;

import com.al7afez.al7afez.dto.MistakeTypeRequest;
import com.al7afez.al7afez.dto.MistakeTypeResponse;
import com.al7afez.al7afez.model.entities.MistakeType;
import com.al7afez.al7afez.repositories.MistakeTypeRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class MistakeTypeService extends AbsMasterFileService<MistakeType> {
    private final MistakeTypeRepository repository;
    private final MappingService mappingService;

    public MistakeTypeService(MistakeTypeRepository repository, MappingService mappingService) {
        this.repository = repository;
        this.mappingService = mappingService;
    }


    @Override
    protected void isValidForDelete(MistakeType entity) {
        if (repository.existsByParentId(entity.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Delete or reassign child mistake types first");
        }
    }

    public Page<MistakeTypeResponse> getAll(Pageable pageable) {
        return repository.findAllWithParent(pageable).map(this::toResponse);
    }

    public MistakeTypeResponse getById(Long id) {
        MistakeType mistakeType = repository.findByIdWithParent(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Mistake type not found"));
        return toResponse(mistakeType);
    }

    public MistakeTypeResponse create(MistakeTypeRequest request) {
        MistakeType mistakeType = new MistakeType();
        apply(mistakeType, request);
        MistakeType saved = save(mistakeType, repository);
        return toResponse(saved);
    }

    public MistakeTypeResponse update(Long id, MistakeTypeRequest request) {
        MistakeType mistakeType = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Mistake type not found"));
        apply(mistakeType, request);
        MistakeType saved = save(mistakeType, repository);
        return toResponse(saved);
    }

    public void delete(Long id) {
        MistakeType mistakeType = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Mistake type not found"));
        isValidForDelete(mistakeType);
        repository.delete(mistakeType);
    }

    private void apply(MistakeType mistakeType, MistakeTypeRequest request) {
        mistakeType.setName(request.name().trim());
        mistakeType.setCode(normalize(request.code()));
        mistakeType.setParent(resolveParent(request.parentId()));
    }

    private MistakeType resolveParent(Long parentId) {
        if (parentId == null) return null;
        return repository.findById(parentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Selected parent type was not found"));
    }

    private MistakeTypeResponse toResponse(MistakeType mistakeType) {
        return new MistakeTypeResponse(
                mistakeType.getId(),
                mistakeType.getCode(),
                mistakeType.getName(),
                mappingService.toEntityReferenceData(mistakeType.getParent())
        );
    }
}