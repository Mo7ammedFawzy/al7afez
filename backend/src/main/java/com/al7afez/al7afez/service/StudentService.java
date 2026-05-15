package com.al7afez.al7afez.service;

import com.al7afez.al7afez.dto.StudentRequest;
import com.al7afez.al7afez.dto.StudentResponse;
import com.al7afez.al7afez.infra.SecurityService;
import com.al7afez.al7afez.model.entities.Sheikh;
import com.al7afez.al7afez.model.entities.Student;
import com.al7afez.al7afez.repositories.StudentRepository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import com.al7afez.al7afez.infra.Messages;
import org.springframework.web.server.ResponseStatusException;

@Service
public class StudentService extends AbsMasterFileService<Student> {
    private final StudentRepository studentRepository;
    private final MappingService mappingService;
    private final SecurityService securityService;

    public StudentService(StudentRepository studentRepository, MappingService mappingService, SecurityService securityService) {
        this.studentRepository = studentRepository;
        this.mappingService = mappingService;
        this.securityService = securityService;
    }

    public Page<StudentResponse> getAll(Pageable pageable, Long groupId) {
        Optional<Sheikh> currentSheikh = securityService.getCurrentSheikh();
        Page<Student> page;
        if (groupId != null) {
            page = studentRepository.findAllWithGroupByGroup(groupId, pageable);
        } else if (currentSheikh.isPresent()) {
            page = studentRepository.findAllWithGroupBySheikh(currentSheikh.get().getId(), pageable);
        } else {
            page = studentRepository.findAllWithGroup(pageable);
        }
        return page.map(mappingService::toStudentResponse);
    }

    public StudentResponse getById(Long id) {
        Student student = studentRepository.findByIdWithGroup(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, Messages.get("error.student.notFound")));
        return mappingService.toStudentResponse(student);
    }

    public StudentResponse create(StudentRequest request) {
        Student student = new Student();
        mappingService.toStudent(student, request);
        Student saved = save(student, studentRepository);
        return mappingService.toStudentResponse(saved);
    }

    public StudentResponse update(Long id, StudentRequest request) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, Messages.get("error.student.notFound")));
        mappingService.toStudent(student, request);
        Student saved = save(student, studentRepository);
        return mappingService.toStudentResponse(saved);
    }

    public void delete(Long id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, Messages.get("error.student.notFound")));
        isValidForDelete(student);
        studentRepository.delete(student);
    }
}