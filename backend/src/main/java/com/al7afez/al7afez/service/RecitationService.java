package com.al7afez.al7afez.service;

import com.al7afez.al7afez.dto.RecitationMistakeRequest;
import com.al7afez.al7afez.dto.RecitationResponse;
import com.al7afez.al7afez.dto.RecitationRequest;
import com.al7afez.al7afez.model.entities.MistakeType;
import com.al7afez.al7afez.model.entities.RecitationDocument;
import com.al7afez.al7afez.model.details.RecitationMistakeLine;
import com.al7afez.al7afez.model.entities.Student;
import com.al7afez.al7afez.repositories.MistakeTypeRepository;
import com.al7afez.al7afez.repositories.RecitationDocumentRepository;
import com.al7afez.al7afez.repositories.StudentRepository;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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
    private final MistakeTypeRepository mistakeTypeRepository;
    private final MappingService mappingService;

    public RecitationService(
            RecitationDocumentRepository recitationRepository,
            StudentRepository studentRepository,
            MistakeTypeRepository mistakeTypeRepository,
            MappingService mappingService
    ) {
        this.recitationRepository = recitationRepository;
        this.studentRepository = studentRepository;
        this.mistakeTypeRepository = mistakeTypeRepository;
        this.mappingService = mappingService;
    }

    public Page<RecitationResponse> getAll(Pageable pageable) {
        return recitationRepository.findAllDetailed(pageable).map(mappingService::toRecitationResponse);
    }

    public RecitationResponse getById(Long id) {
        RecitationDocument recitation = recitationRepository.findByIdDetailed(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Recitation not found"));
        return mappingService.toRecitationResponse(recitation);
    }

    public RecitationResponse create(RecitationRequest request) {
        RecitationDocument recitation = new RecitationDocument();
        apply(recitation, request);
        RecitationDocument saved = save(recitation, recitationRepository);
        return mappingService.toRecitationResponse(saved);
    }

    public RecitationResponse update(Long id, RecitationRequest request) {
        RecitationDocument recitation = recitationRepository.findByIdDetailed(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Recitation not found"));
        apply(recitation, request);
        RecitationDocument saved = save(recitation, recitationRepository);
        return mappingService.toRecitationResponse(saved);
    }

    public void delete(Long id) {
        RecitationDocument recitation = recitationRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Recitation not found"));
        isValidForDelete(recitation);
        recitationRepository.delete(recitation);
    }

    public List<RecitationResponse> getRecent(int limit) {
        return recitationRepository.findRecent(Pageable.ofSize(limit)).stream()
                .map(mappingService::toRecitationResponse)
                .toList();
    }

    private void apply(RecitationDocument recitation, RecitationRequest request) {
        Student student = studentRepository.findByIdWithGroup(request.studentId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Selected student was not found"));

        recitation.setCode(normalize(request.code()));
        recitation.setStudent(student);
        recitation.setRecitationDate(request.recitationDate() != null ? request.recitationDate() : LocalDate.now());
        recitation.setFromSurah(request.fromSurah());
        recitation.setToSurah(request.toSurah());
        recitation.setFromAya(request.fromAya());
        recitation.setToAya(request.toAya());
        recitation.setNumberOfAyat(request.numberOfAyat());
        recitation.setGrade(request.grade());
        recitation.setNotes(normalize(request.notes()));
        recitation.setMistakes(buildMistakes(recitation, request.mistakes()));
    }

    private List<RecitationMistakeLine> buildMistakes(RecitationDocument recitation, List<RecitationMistakeRequest> requests) {
        List<RecitationMistakeLine> mistakes = new ArrayList<>();
        if (requests == null) {
            return mistakes;
        }

        for (RecitationMistakeRequest request : requests) {
            if (request == null || request.mistakeTypeId() == null || request.count() == null) {
                continue;
            }
            if (request.count() <= 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Mistake counts must be greater than zero");
            }
            MistakeType mistakeType = mistakeTypeRepository.findById(request.mistakeTypeId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Selected mistake type was not found"));
            RecitationMistakeLine mistake = new RecitationMistakeLine();
            mistake.setRecitationDocument(recitation);
            mistake.setMistakeType(mistakeType);
            mistake.setCount(request.count());
            mistake.setNote(normalize(request.note()));
            mistakes.add(mistake);
        }

        return mistakes.stream().filter(Objects::nonNull).toList();
    }
}