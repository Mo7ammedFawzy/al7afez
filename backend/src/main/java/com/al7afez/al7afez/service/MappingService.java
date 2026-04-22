package com.al7afez.al7afez.service;

import com.al7afez.al7afez.dto.GroupResponse;
import com.al7afez.al7afez.dto.EntityReference;
import com.al7afez.al7afez.dto.RecitationMistakeResponse;
import com.al7afez.al7afez.dto.RecitationResponse;
import com.al7afez.al7afez.dto.StudentResponse;
import com.al7afez.al7afez.model.entities.Level;
import com.al7afez.al7afez.model.entities.MasterFile;
import com.al7afez.al7afez.model.entities.RecitationDocument;
import com.al7afez.al7afez.model.entities.RecitationGroup;
import com.al7afez.al7afez.model.details.RecitationMistakeLine;
import com.al7afez.al7afez.model.entities.Sheikh;
import com.al7afez.al7afez.model.entities.Student;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;

@Service
public class MappingService {
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

    public GroupResponse toGroupResponse(RecitationGroup group, List<Student> students) {
        List<EntityReference> studentsRefs = students.stream()
                .map(this::toEntityReferenceData)
                .toList();

        return new GroupResponse(
                group.getId(),
                group.getCode(),
                group.getName(),
                toEntityReferenceData(group.getLevel()),
                toEntityReferenceData(group.getSheikh()),
                studentsRefs,
                studentsRefs.size()
        );
    }

    public RecitationMistakeResponse toRecitationMistakeResponse(RecitationMistakeLine mistake) {
        return new RecitationMistakeResponse(
                mistake.getId(),
                toEntityReferenceData(mistake.getMistakeType()),
                mistake.getCount(),
                mistake.getNote()
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
}
