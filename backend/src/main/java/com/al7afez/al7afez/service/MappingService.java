package com.al7afez.al7afez.service;

import com.al7afez.al7afez.dto.*;
import com.al7afez.al7afez.model.details.RecitationMistakeLine;
import com.al7afez.al7afez.model.entities.*;
import com.al7afez.al7afez.repositories.GroupRepository;
import com.al7afez.al7afez.repositories.LevelRepository;
import com.al7afez.al7afez.repositories.MistakeTypeRepository;
import com.al7afez.al7afez.repositories.SheikhRepository;
import com.al7afez.al7afez.repositories.StudentRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class MappingService {

    private final LevelRepository levelRepository;
    private final SheikhRepository sheikhRepository;
    private final GroupRepository groupRepository;
    private final StudentRepository studentRepository;
    private final MistakeTypeRepository mistakeTypeRepository;

    public MappingService(
            LevelRepository levelRepository,
            SheikhRepository sheikhRepository,
            GroupRepository groupRepository,
            StudentRepository studentRepository,
            MistakeTypeRepository mistakeTypeRepository
    ) {
        this.levelRepository = levelRepository;
        this.sheikhRepository = sheikhRepository;
        this.groupRepository = groupRepository;
        this.studentRepository = studentRepository;
        this.mistakeTypeRepository = mistakeTypeRepository;
    }

    // ── Entity → Response ──────────────────────────────────────────────────────

    public EntityReference toEntityReferenceData(MasterFile entity) {
        if (entity == null) {
            return null;
        }
        return new EntityReference(entity.getId(), entity.getName());
    }

    public StudentResponse toStudentResponse(Student student) {
        RecitationGroup group = student.getRecitationGroup();
        Level level = group != null ? group.getLevel() : null;
        return new StudentResponse(
                student.getId(),
                student.getCode(),
                student.getName(),
                student.getBirthDate(),
                student.getPhoneNumber(),
                student.getParentPhoneNumber(),
                student.getGender(),
                toEntityReferenceData(group),
                toEntityReferenceData(level)
        );
    }

    public GroupResponse toGroupResponse(RecitationGroup group) {
        return new GroupResponse(
                group.getId(),
                group.getCode(),
                group.getName(),
                toEntityReferenceData(group.getLevel()),
                toEntityReferenceData(group.getSheikh())
        );
    }

    public RecitationMistakeResponse toRecitationMistakeResponse(RecitationMistakeLine mistake) {
        return new RecitationMistakeResponse(
                mistake.getId(),
                toEntityReferenceData(mistake.getMistakeType()),
                mistake.getCount()
        );
    }

    public RecitationResponse toRecitationResponse(RecitationDocument recitation) {
        Student student = recitation.getStudent();
        RecitationGroup group = student != null ? student.getRecitationGroup() : null;
        Level level = group != null ? group.getLevel() : null;
        Sheikh sheikh = group != null ? group.getSheikh() : null;
        List<RecitationMistakeResponse> mistakes = recitation.getMistakes().stream()
                .filter(Objects::nonNull)
                .map(this::toRecitationMistakeResponse)
                .toList();
        int totalMistakes = recitation.getMistakes().stream()
                .map(RecitationMistakeLine::getCount)
                .filter(Objects::nonNull)
                .mapToInt(Integer::intValue)
                .sum();

        return new RecitationResponse(
                recitation.getId(),
                recitation.getCode(),
                recitation.getRecitationDate(),
                toEntityReferenceData(student),
                toEntityReferenceData(group),
                toEntityReferenceData(level),
                toEntityReferenceData(sheikh),
                recitation.getFromSurah(),
                recitation.getToSurah(),
                recitation.getFromAya(),
                recitation.getToAya(),
                recitation.getNumberOfAyat(),
                recitation.getGrade(),
                totalMistakes,
                recitation.getNotes(),
                mistakes
        );
    }

    public UserResponse toUserResponse(AppUser user) {
        return new UserResponse(
                user.getId(),
                user.getCode(),
                user.getName(),
                user.getUsername(),
                toEntityReferenceData(user.getSheikh())
        );
    }
    // ── Request → Entity ───────────────────────────────────────────────────────

    public void toRecitationGroup(RecitationGroup group, GroupRequest request) {
        group.setName(request.name().trim());
        group.setCode(normalize(request.code()));
        group.setLevel(resolveLevel(request.levelId()));
        group.setSheikh(resolveSheikh(request.sheikhId()));
    }

    public void toStudent(Student student, StudentRequest request) {
        student.setName(request.name().trim());
        student.setCode(normalize(request.code()));
        student.setBirthDate(request.birthDate());
        student.setPhoneNumber(normalize(request.phoneNumber()));
        student.setParentPhoneNumber(normalize(request.parentPhoneNumber()));
        student.setGender(request.gender());
        student.setRecitationGroup(resolveGroup(request.recitationGroupId()));
    }

    public void toSheikh(Sheikh sheikh, SheikhRequest request) {
        sheikh.setName(request.name().trim());
        sheikh.setCode(normalize(request.code()));
        sheikh.setBirthDate(request.birthDate());
        sheikh.setPhoneNumber(normalize(request.phoneNumber()));
        sheikh.setGender(request.gender());
    }

    public void toUser(AppUser user, UserRequest request) {
        user.setName(request.name().trim());
        user.setCode(normalize(request.code()));
        user.setUsername(normalize(request.username()));
        user.setSheikh(resolveSheikh(request.sheikhId()));
    }

    public void toLevel(Level level, LevelRequest request) {
        level.setName(request.name().trim());
        level.setCode(normalize(request.code()));
        level.setFromSurah(request.fromSurah());
        level.setToSurah(request.toSurah());
        level.setFromAya(request.fromAya());
        level.setToAya(request.toAya());
        level.setNumberOfAyatPerSession(request.numberOfAyatPerSession());
    }

    public void toMistakeType(MistakeType mistakeType, MistakeTypeRequest request) {
        mistakeType.setName(request.name().trim());
        mistakeType.setCode(normalize(request.code()));
        mistakeType.setParent(resolveMistakeType(request.parentId()));
    }

    public void toRecitation(RecitationDocument recitation, RecitationRequest request) {
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
        recitation.setMistakes(buildMistakeLines(recitation, request.mistakes()));
    }

    // ── Private helpers ────────────────────────────────────────────────────────

    private List<RecitationMistakeLine> buildMistakeLines(RecitationDocument recitation, List<RecitationMistakeRequest> requests) {
        if (requests == null) return new ArrayList<>();
        List<RecitationMistakeLine> lines = new ArrayList<>();
        for (RecitationMistakeRequest req : requests) {
            if (req == null || req.mistakeTypeId() == null || req.count() == null) continue;
            if (req.count() <= 0)
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Mistake counts must be greater than zero");
            RecitationMistakeLine line = new RecitationMistakeLine();
            line.setRecitationDocument(recitation);
            line.setMistakeType(resolveMistakeType(req.mistakeTypeId()));
            line.setCount(req.count());
            lines.add(line);
        }
        return lines.stream().filter(Objects::nonNull).toList();
    }

    private Level resolveLevel(Long id) {
        if (id == null) return null;
        return levelRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Selected level was not found"));
    }

    private Sheikh resolveSheikh(Long id) {
        if (id == null) return null;
        return sheikhRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Selected sheikh was not found"));
    }

    private RecitationGroup resolveGroup(Long id) {
        if (id == null) return null;
        return groupRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Selected group was not found"));
    }

    private MistakeType resolveMistakeType(Long id) {
        if (id == null) return null;
        return mistakeTypeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Selected mistake type was not found"));
    }

    private String normalize(String value) {
        if (value == null) return null;
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}