package com.al7afez.al7afez.repositories;

import com.al7afez.al7afez.entities.Student;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface StudentRepository extends BaseRepository<Student, Long> {
    @Query("select s from Student s")
    @EntityGraph(attributePaths = {"recitationGroup", "recitationGroup.level"})
    Page<Student> findAllWithGroup(Pageable pageable);

    @Query("select s from Student s where s.id = :id")
    @EntityGraph(attributePaths = {"recitationGroup", "recitationGroup.level"})
    Optional<Student> findByIdWithGroup(@Param("id") Long id);

    @Query("select s from Student s")
    @EntityGraph(attributePaths = {"recitationGroup", "recitationGroup.level"})
    List<Student> findAllWithGroup();

    List<Student> findByRecitationGroupIdOrderByNameAsc(Long recitationGroupId);
}
