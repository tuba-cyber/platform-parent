package com.platform.co.branch.repository;

import com.platform.co.branch.entity.Branch;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BranchRepository extends JpaRepository<Branch, UUID> {

    List<Branch> findByCompanyIdAndActiveTrue(UUID companyId);

    Optional<Branch> findByIdAndActiveTrue(UUID id);

    Optional<Branch> findByCompanyIdAndHeadquartersTrue(UUID companyId);

    boolean existsByCode(String code);
}
