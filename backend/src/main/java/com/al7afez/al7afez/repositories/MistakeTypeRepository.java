package com.al7afez.al7afez.repositories;

import com.al7afez.al7afez.model.entities.MistakeType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MistakeTypeRepository extends BaseRepository<MistakeType, Long> {
    @Query("select m from MistakeType m")
    @EntityGraph(attributePaths = {"parent"})
    Page<MistakeType> findAllWithParent(Pageable pageable);

    @Query("select m from MistakeType m where m.id = :id")
    @EntityGraph(attributePaths = {"parent"})
    Optional<MistakeType> findByIdWithParent(@Param("id") Long id);

    @Query("select m from MistakeType m")
    @EntityGraph(attributePaths = {"parent"})
    List<MistakeType> findAllWithParent();

    boolean existsByParentId(Long parentId);
}
