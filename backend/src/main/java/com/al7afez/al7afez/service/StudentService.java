package com.al7afez.al7afez.service;

import com.al7afez.al7afez.dto.StudentRequest;
import com.al7afez.al7afez.dto.StudentResponse;
import com.al7afez.al7afez.model.entities.RecitationGroup;
import com.al7afez.al7afez.model.entities.Student;
import com.al7afez.al7afez.repositories.GroupRepository;
import com.al7afez.al7afez.repositories.StudentRepository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class StudentService extends AbsMasterFileService<Student> {
    private final StudentRepository studentRepository;
    private final GroupRepository groupRepository;
    private final MappingService mappingService;

    public StudentService(StudentRepository studentRepository, GroupRepository groupRepository, MappingService mappingService) {
        this.studentRepository = studentRepository;
        this.groupRepository = groupRepository;
        this.mappingService = mappingService;
    }

    public Page<StudentResponse> getAll(Pageable pageable) {
        return studentRepository.findAllWithGroup(pageable).map(mappingService::toStudentResponse);
    }

    public StudentResponse getById(Long id) {
        Student student = studentRepository.findByIdWithGroup(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found"));
        return mappingService.toStudentResponse(student);
    }

    public StudentResponse create(StudentRequest request) {
        Student student = new Student();
        apply(student, request);
        Student saved = save(student, studentRepository);
        return mappingService.toStudentResponse(saved);
    }

    public StudentResponse update(Long id, StudentRequest request) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found"));
        apply(student, request);
        Student saved = save(student, studentRepository);
        return mappingService.toStudentResponse(saved);
    }

    public void delete(Long id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found"));
        isValidForDelete(student);
        studentRepository.delete(student);
    }

    private void apply(Student student, StudentRequest request) {
        student.setName(request.name().trim());
        student.setCode(normalize(request.code()));
        student.setBirthDate(request.birthDate());
        student.setPhoneNumber(normalize(request.phoneNumber()));
        student.setParentPhoneNumber(normalize(request.parentPhoneNumber()));
        student.setGender(request.gender());

        Optional<RecitationGroup> recitationGroup = request.recitationGroupId() == null
                ? Optional.empty()
                : groupRepository.findById(request.recitationGroupId());
        if (request.recitationGroupId() != null && recitationGroup.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Selected group was not found");
        }
        student.setRecitationGroup(recitationGroup.orElse(null));
    }
}