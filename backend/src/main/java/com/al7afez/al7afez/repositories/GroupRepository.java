package com.al7afez.al7afez.repositories;

import com.al7afez.al7afez.model.entities.RecitationGroup;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface GroupRepository extends BaseRepository<RecitationGroup, Long> {
    @Query("select g from RecitationGroup g")
    @EntityGraph(attributePaths = {"level", "sheikh"})
    Page<RecitationGroup> findAllWithDetails(Pageable pageable);

    @Query("select g from RecitationGroup g where g.id = :id")
    @EntityGraph(attributePaths = {"level", "sheikh"})
    Optional<RecitationGroup> findByIdWithDetails(@Param("id") Long id);

    @Query("select g from RecitationGroup g")
    @EntityGraph(attributePaths = {"level", "sheikh"})
    List<RecitationGroup> findAllWithDetails();
}
