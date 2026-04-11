package com.platform.hr.employee.repository;

import com.platform.hr.employee.entity.Employee;
import com.platform.hr.employee.entity.EmployeeStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, UUID> {
    List<Employee> findByCompanyId(UUID companyId);
    List<Employee> findByCompanyIdAndStatus(UUID companyId, EmployeeStatus status);
    List<Employee> findByDepartmentId(UUID departmentId);
    List<Employee> findByPositionId(UUID positionId);
    Optional<Employee> findByEmployeeNumber(String employeeNumber);
    Optional<Employee> findByNationalId(String nationalId);
    boolean existsByEmployeeNumber(String employeeNumber);
    boolean existsByNationalId(String nationalId);

    @Query("SELECT e FROM Employee e WHERE e.companyId = :companyId AND " +
           "(LOWER(e.firstName) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
           " LOWER(e.lastName) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
           " LOWER(e.employeeNumber) LIKE LOWER(CONCAT('%', :q, '%')))")
    List<Employee> search(@Param("companyId") UUID companyId, @Param("q") String query);
}
