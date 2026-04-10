package com.platform.co.company.repository;

import com.platform.co.company.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CompanyRepository extends JpaRepository<Company, UUID> {

    List<Company> findByActiveTrue();

    Optional<Company> findByIdAndActiveTrue(UUID id);

    Optional<Company> findByCode(String code);

    boolean existsByCode(String code);

    boolean existsByName(String name);

    @Query("SELECT c FROM Company c WHERE c.active = true AND " +
           "(LOWER(c.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(c.city) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(c.sector) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<Company> search(String keyword);
}
