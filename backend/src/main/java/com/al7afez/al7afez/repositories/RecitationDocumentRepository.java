package com.al7afez.al7afez.repositories;

import com.al7afez.al7afez.model.entities.RecitationDocument;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RecitationDocumentRepository extends BaseRepository<RecitationDocument, Long> {
    @Query("select r from RecitationDocument r")
    @EntityGraph(attributePaths = {
            "student",
            "student.recitationGroup",
            "student.recitationGroup.level",
            "student.recitationGroup.sheikh",
            "mistakes",
            "mistakes.mistakeType"
    })
    Page<RecitationDocument> findAllDetailed(Pageable pageable);

    @Query("select r from RecitationDocument r where r.id = :id")
    @EntityGraph(attributePaths = {
            "student",
            "student.recitationGroup",
            "student.recitationGroup.level",
            "student.recitationGroup.sheikh",
            "mistakes",
            "mistakes.mistakeType"
    })
    Optional<RecitationDocument> findByIdDetailed(@Param("id") Long id);

    @Query("select r from RecitationDocument r")
    @EntityGraph(attributePaths = {
            "student",
            "student.recitationGroup",
            "student.recitationGroup.level",
            "student.recitationGroup.sheikh",
            "mistakes",
            "mistakes.mistakeType"
    })
    List<RecitationDocument> findAllDetailed();

    @EntityGraph(attributePaths = {
            "student",
            "student.recitationGroup",
            "student.recitationGroup.level",
            "student.recitationGroup.sheikh",
            "mistakes",
            "mistakes.mistakeType"
    })
    List<RecitationDocument> findByOrderByRecitationDateDescIdDesc(Pageable pageable);

    default List<RecitationDocument> findRecent(Pageable pageable) {
        return findByOrderByRecitationDateDescIdDesc(pageable);
    }

    @Query("select r from RecitationDocument r where r.student.id = :studentId order by r.recitationDate desc, r.id desc")
    @EntityGraph(attributePaths = {
            "student",
            "student.recitationGroup",
            "student.recitationGroup.level",
            "student.recitationGroup.sheikh",
            "mistakes",
            "mistakes.mistakeType"
    })
    Page<RecitationDocument> findByStudentIdDetailed(@Param("studentId") Long studentId, Pageable pageable);
}
