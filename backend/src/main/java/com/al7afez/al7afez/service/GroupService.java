package com.al7afez.al7afez.service;

import com.al7afez.al7afez.dto.GroupRequest;
import com.al7afez.al7afez.dto.GroupResponse;
import com.al7afez.al7afez.infra.Messages;
import com.al7afez.al7afez.infra.ObjectChecker;
import com.al7afez.al7afez.infra.SecurityService;
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
    private final SecurityService securityService;

    public GroupService(
            GroupRepository groupRepository,
            StudentRepository studentRepository,
            MappingService mappingService,
            SecurityService securityService
    ) {
        this.groupRepository = groupRepository;
        this.studentRepository = studentRepository;
        this.mappingService = mappingService;
        this.securityService = securityService;
    }


    @Override
    protected void isValidForDelete(RecitationGroup entity) {
        List<Student> students = studentRepository.findByRecitationGroupIdOrderByNameAsc(entity.getId());
        if (ObjectChecker.isEmptyOrNull(students)) return;
        String codes = students.stream()
                .map(s -> s.getCode() != null ? s.getCode() : s.getName())
                .collect(Collectors.joining(", "));
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                Messages.get("error.group.hasStudents", codes));
    }

    public Page<GroupResponse> getAll(Pageable pageable) {
        return securityService.getCurrentSheikh()
                .map(sheikh -> groupRepository.findAllWithDetailsBySheikh(sheikh.getId(), pageable))
                .orElseGet(() -> groupRepository.findAllWithDetails(pageable))
                .map(mappingService::toGroupResponse);
    }

    public GroupResponse getById(Long id) {
        RecitationGroup group = groupRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, Messages.get("error.group.notFound")));
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
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, Messages.get("error.group.notFound")));
        mappingService.toRecitationGroup(group, request);
        RecitationGroup saved = save(group, groupRepository);
        return mappingService.toGroupResponse(saved);
    }

    public void delete(Long id) {
        RecitationGroup group = groupRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, Messages.get("error.group.notFound")));
        isValidForDelete(group);
        groupRepository.delete(group);
    }
}