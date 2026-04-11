package com.platform.hr.position.repository;

import com.platform.hr.position.entity.Position;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PositionRepository extends JpaRepository<Position, UUID> {
    List<Position> findByCompanyIdAndActiveTrue(UUID companyId);
    List<Position> findByDepartmentIdAndActiveTrue(UUID departmentId);
    Optional<Position> findByCode(String code);
    boolean existsByCode(String code);
    boolean existsByCodeAndIdNot(String code, UUID id);
}
