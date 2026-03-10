package com.platform.moduleengine.section.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.platform.moduleengine.section.entity.Section;

@Repository
public interface SectionRepository extends JpaRepository<Section, UUID> {

    List<Section> findByModuleIdOrderByOrderIndexAsc(UUID moduleId);

    List<Section> findByModuleIdAndActiveOrderByOrderIndexAsc(
        UUID moduleId, Boolean active
    );

    @Query("""
        SELECT s FROM Section s
        LEFT JOIN FETCH s.screens
        WHERE s.id = :id
    """)
    Optional<Section> findByIdWithScreens(@Param("id") UUID id);
}