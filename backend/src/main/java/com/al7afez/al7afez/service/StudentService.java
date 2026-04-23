package com.al7afez.al7afez.service;

import com.al7afez.al7afez.dto.StudentRequest;
import com.al7afez.al7afez.dto.StudentResponse;
import com.al7afez.al7afez.model.entities.Student;
import com.al7afez.al7afez.repositories.StudentRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class StudentService extends AbsMasterFileService<Student> {
    private final StudentRepository studentRepository;
    private final MappingService mappingService;

    public StudentService(StudentRepository studentRepository, MappingService mappingService) {
        this.studentRepository = studentRepository;
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
        mappingService.toStudent(student, request);
        Student saved = save(student, studentRepository);
        return mappingService.toStudentResponse(saved);
    }

    public StudentResponse update(Long id, StudentRequest request) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found"));
        mappingService.toStudent(student, request);
        Student saved = save(student, studentRepository);
        return mappingService.toStudentResponse(saved);
    }

    public void delete(Long id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found"));
        isValidForDelete(student);
        studentRepository.delete(student);
    }
}