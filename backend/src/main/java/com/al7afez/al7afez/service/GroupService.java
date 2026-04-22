package com.al7afez.al7afez.service;

import com.al7afez.al7afez.dto.GroupRequest;
import com.al7afez.al7afez.dto.GroupResponse;
import com.al7afez.al7afez.model.entities.Level;
import com.al7afez.al7afez.model.entities.RecitationGroup;
import com.al7afez.al7afez.model.entities.Sheikh;
import com.al7afez.al7afez.model.entities.Student;
import com.al7afez.al7afez.repositories.GroupRepository;
import com.al7afez.al7afez.repositories.LevelRepository;
import com.al7afez.al7afez.repositories.SheikhRepository;
import com.al7afez.al7afez.repositories.StudentRepository;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional
public class GroupService {
    private final GroupRepository groupRepository;
    private final LevelRepository levelRepository;
    private final SheikhRepository sheikhRepository;
    private final StudentRepository studentRepository;
    private final MappingService mappingService;

    public GroupService(
            GroupRepository groupRepository,
            LevelRepository levelRepository,
            SheikhRepository sheikhRepository,
            StudentRepository studentRepository,
            MappingService mappingService
    ) {
        this.groupRepository = groupRepository;
        this.levelRepository = levelRepository;
        this.sheikhRepository = sheikhRepository;
        this.studentRepository = studentRepository;
        this.mappingService = mappingService;
    }

    public Page<GroupResponse> getAll(Pageable pageable) {
        return groupRepository.findAllWithDetails(pageable)
                .map(group -> mappingService.toGroupResponse(group, studentRepository.findByRecitationGroupIdOrderByNameAsc(group.getId())));
    }

    public GroupResponse getById(Long id) {
        RecitationGroup group = groupRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Group not found"));
        return mappingService.toGroupResponse(group, studentRepository.findByRecitationGroupIdOrderByNameAsc(id));
    }

    public GroupResponse create(GroupRequest request) {
        RecitationGroup group = new RecitationGroup();
        apply(group, request);
        RecitationGroup saved = groupRepository.save(group);
        syncStudents(saved, request.studentIds());
        return getById(saved.getId());
    }

    public GroupResponse update(Long id, GroupRequest request) {
        RecitationGroup group = groupRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Group not found"));
        apply(group, request);
        RecitationGroup saved = groupRepository.save(group);
        syncStudents(saved, request.studentIds());
        return getById(saved.getId());
    }

    public void delete(Long id) {
        RecitationGroup group = groupRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Group not found"));
        List<Student> students = studentRepository.findByRecitationGroupIdOrderByNameAsc(id);
        students.forEach(student -> student.setRecitationGroup(null));
        studentRepository.saveAll(students);
        groupRepository.delete(group);
    }

    private void apply(RecitationGroup group, GroupRequest request) {
        if (request == null || request.name() == null || request.name().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Group name is required");
        }
        group.setName(request.name().trim());
        group.setCode(normalize(request.code()));
        group.setLevel(resolveLevel(request.levelId()));
        group.setSheikh(resolveSheikh(request.sheikhId()));
    }

    private void syncStudents(RecitationGroup group, List<Long> studentIds) {
        Set<Long> requestedIds = studentIds == null ? Set.of() : new HashSet<>(studentIds);
        List<Student> currentStudents = studentRepository.findByRecitationGroupIdOrderByNameAsc(group.getId());
        List<Student> changedStudents = new ArrayList<>();

        for (Student student : currentStudents) {
            if (!requestedIds.contains(student.getId())) {
                student.setRecitationGroup(null);
                changedStudents.add(student);
            }
        }

        if (!requestedIds.isEmpty()) {
            List<Student> selectedStudents = studentRepository.findAllById(requestedIds);
            if (selectedStudents.size() != requestedIds.size()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "One or more selected students were not found");
            }
            for (Student student : selectedStudents) {
                student.setRecitationGroup(group);
                changedStudents.add(student);
            }
        }

        if (!changedStudents.isEmpty()) {
            studentRepository.saveAll(changedStudents);
        }
    }

    private Level resolveLevel(Long id) {
        if (id == null) {
            return null;
        }
        return levelRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Selected level was not found"));
    }

    private Sheikh resolveSheikh(Long id) {
        if (id == null) {
            return null;
        }
        return sheikhRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Selected sheikh was not found"));
    }

    private String normalize(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
