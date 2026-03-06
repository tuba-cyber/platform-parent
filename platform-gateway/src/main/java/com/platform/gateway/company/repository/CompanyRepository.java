package com.platform.gateway.company.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.platform.gateway.company.entity.Company;

@Repository
public interface CompanyRepository extends JpaRepository<Company, UUID> {

    Optional<Company> findByCode(String code);
    
    Optional<Company> findByName(String name);
    
    boolean existsByCode(String code);
    
    boolean existsByName(String name);
}