package com.platform.moduleengine.module.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.platform.moduleengine.module.entity.Module;

@Repository
public interface ModuleRepository extends JpaRepository<Module, UUID> {

    List<Module> findByCompanyIdOrderByOrderIndexAsc(String companyId);

    List<Module> findByCompanyIdAndActiveOrderByOrderIndexAsc(
        String companyId, Boolean active
    );

    Optional<Module> findByCode(String code);

    boolean existsByCode(String code);

    @Query("""
        SELECT m FROM Module m
        LEFT JOIN FETCH m.sections s
        LEFT JOIN FETCH s.screens
        WHERE m.id = :id
    """)
    Optional<Module> findByIdWithSectionsAndScreens(@Param("id") UUID id);
}