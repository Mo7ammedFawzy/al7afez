package com.al7afez.al7afez.service;

import com.al7afez.al7afez.dto.RecitationRequest;
import com.al7afez.al7afez.infra.Messages;
import com.al7afez.al7afez.dto.RecitationResponse;
import com.al7afez.al7afez.dto.RecitationSuggestionResponse;
import com.al7afez.al7afez.model.entities.Level;
import com.al7afez.al7afez.model.entities.RecitationDocument;
import com.al7afez.al7afez.repositories.RecitationDocumentRepository;
import com.al7afez.al7afez.repositories.StudentRepository;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional
public class RecitationService extends AbsDocumentFileService<RecitationDocument> {
    private final RecitationDocumentRepository recitationRepository;
    private final StudentRepository studentRepository;
    private final MappingService mappingService;

    public RecitationService(
            RecitationDocumentRepository recitationRepository,
            StudentRepository studentRepository,
            MappingService mappingService
    ) {
        this.recitationRepository = recitationRepository;
        this.studentRepository = studentRepository;
        this.mappingService = mappingService;
    }

    public Page<RecitationResponse> getAll(Pageable pageable) {
        return recitationRepository.findAllDetailed(pageable).map(mappingService::toRecitationResponse);
    }

    public RecitationResponse getById(Long id) {
        RecitationDocument recitation = recitationRepository.findByIdDetailed(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, Messages.get("error.recitation.notFound")));
        return mappingService.toRecitationResponse(recitation);
    }

    public RecitationResponse create(RecitationRequest request) {
        RecitationDocument recitation = new RecitationDocument();
        mappingService.toRecitation(recitation, request);
        RecitationDocument saved = save(recitation, recitationRepository);
        return mappingService.toRecitationResponse(saved);
    }

    public RecitationResponse update(Long id, RecitationRequest request) {
        RecitationDocument recitation = recitationRepository.findByIdDetailed(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, Messages.get("error.recitation.notFound")));
        mappingService.toRecitation(recitation, request);
        RecitationDocument saved = save(recitation, recitationRepository);
        return mappingService.toRecitationResponse(saved);
    }

    public void delete(Long id) {
        RecitationDocument recitation = recitationRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, Messages.get("error.recitation.notFound")));
        isValidForDelete(recitation);
        recitationRepository.delete(recitation);
    }

    public List<RecitationResponse> getRecent(int limit) {
        return recitationRepository.findRecent(Pageable.ofSize(limit)).stream()
                .map(mappingService::toRecitationResponse)
                .toList();
    }

    public Page<RecitationResponse> getByStudentId(Long studentId, Pageable pageable) {
        return recitationRepository.findByStudentIdDetailed(studentId, pageable)
                .map(mappingService::toRecitationResponse);
    }

    public RecitationSuggestionResponse suggestNext(Long studentId) {
        var student = studentRepository.findByIdWithGroup(studentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, Messages.get("error.student.notFound")));

        Level level = student.getRecitationGroup() != null
                ? student.getRecitationGroup().getLevel()
                : null;

        int sessionSize = level != null ? level.getNumberOfAyatPerSession() : 0;

        List<RecitationDocument> latest = recitationRepository.findLatestByStudentId(studentId, Pageable.ofSize(1));

        if (!latest.isEmpty()) {
            return QuranUtils.suggestNextRecitationData(latest.getFirst(), sessionSize);
        }

        if (level != null) {
            return QuranUtils.suggestNextRecitationData(level);
        }

        return new RecitationSuggestionResponse(null, null, null, null, null);
    }
}