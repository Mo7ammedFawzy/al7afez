package com.al7afez.al7afez.service;

import com.al7afez.al7afez.dto.GroupRequest;
import com.al7afez.al7afez.dto.GroupResponse;
import com.al7afez.al7afez.infra.ObjectChecker;
import com.al7afez.al7afez.model.entities.RecitationGroup;
import com.al7afez.al7afez.model.entities.Student;
import com.al7afez.al7afez.repositories.GroupRepository;
import com.al7afez.al7afez.repositories.StudentRepository;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional
public class GroupService extends AbsMasterFileService<RecitationGroup> {
    private final GroupRepository groupRepository;
    private final StudentRepository studentRepository;
    private final MappingService mappingService;

    public GroupService(
            GroupRepository groupRepository,
            StudentRepository studentRepository,
            MappingService mappingService
    ) {
        this.groupRepository = groupRepository;
        this.studentRepository = studentRepository;
        this.mappingService = mappingService;
    }


    @Override
    protected void isValidForDelete(RecitationGroup entity) {
        List<Student> students = studentRepository.findByRecitationGroupIdOrderByNameAsc(entity.getId());
        if (ObjectChecker.isEmptyOrNull(students)) return;
        String codes = students.stream()
                .map(s -> s.getCode() != null ? s.getCode() : s.getName())
                .collect(Collectors.joining(", "));
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "Cannot delete group: it is referenced by the following students: " + codes);
    }

    public Page<GroupResponse> getAll(Pageable pageable) {
        return groupRepository.findAllWithDetails(pageable)
                .map(mappingService::toGroupResponse);
    }

    public GroupResponse getById(Long id) {
        RecitationGroup group = groupRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Group not found"));
        return mappingService.toGroupResponse(group);
    }

    public GroupResponse create(GroupRequest request) {
        RecitationGroup group = new RecitationGroup();
        mappingService.toRecitationGroup(group, request);
        RecitationGroup saved = save(group, groupRepository);
        return mappingService.toGroupResponse(saved);
    }

    public GroupResponse update(Long id, GroupRequest request) {
        RecitationGroup group = groupRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Group not found"));
        mappingService.toRecitationGroup(group, request);
        RecitationGroup saved = save(group, groupRepository);
        return mappingService.toGroupResponse(saved);
    }

    public void delete(Long id) {
        RecitationGroup group = groupRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Group not found"));
        isValidForDelete(group);
        groupRepository.delete(group);
    }
}