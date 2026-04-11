package com.platform.hr.employee.service;

import com.platform.core.common.response.ApiResponse;
import com.platform.hr.employee.dto.EmployeeRequest;
import com.platform.hr.employee.dto.EmployeeResponse;
import com.platform.hr.employee.entity.*;
import com.platform.hr.employee.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    @Transactional
    public ApiResponse<EmployeeResponse> create(EmployeeRequest request) {
        if (request.getEmployeeNumber() != null && employeeRepository.existsByEmployeeNumber(request.getEmployeeNumber())) {
            return ApiResponse.error("Bu sicil numarası zaten kullanılıyor: " + request.getEmployeeNumber());
        }
        if (request.getNationalId() != null && employeeRepository.existsByNationalId(request.getNationalId())) {
            return ApiResponse.error("Bu TC kimlik numarası zaten kayıtlı");
        }

        Employee employee = Employee.builder()
                .companyId(request.getCompanyId())
                .departmentId(request.getDepartmentId())
                .positionId(request.getPositionId())
                .employeeNumber(request.getEmployeeNumber())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .nationalId(request.getNationalId())
                .birthDate(request.getBirthDate())
                .gender(request.getGender())
                .phone(request.getPhone())
                .personalEmail(request.getPersonalEmail())
                .workEmail(request.getWorkEmail())
                .address(request.getAddress())
                .city(request.getCity())
                .district(request.getDistrict())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .employmentType(request.getEmploymentType() != null ? request.getEmploymentType() : EmploymentType.TAM_ZAMANLI)
                .status(request.getStatus() != null ? request.getStatus() : EmployeeStatus.AKTIF)
                .salary(request.getSalary())
                .salaryCurrency(request.getSalaryCurrency() != null ? request.getSalaryCurrency() : "TRY")
                .educationLevel(request.getEducationLevel())
                .school(request.getSchool())
                .schoolDepartment(request.getSchoolDepartment())
                .graduationYear(request.getGraduationYear())
                .emergencyContactName(request.getEmergencyContactName())
                .emergencyContactPhone(request.getEmergencyContactPhone())
                .emergencyContactRelation(request.getEmergencyContactRelation())
                .annualLeaveDays(request.getAnnualLeaveDays() != null ? request.getAnnualLeaveDays() : 14)
                .usedLeaveDays(0)
                .performanceNotes(request.getPerformanceNotes())
                .generalNotes(request.getGeneralNotes())
                .profilePhotoUrl(request.getProfilePhotoUrl())
                .build();

        employee = employeeRepository.save(employee);
        return ApiResponse.success(toResponse(employee), "Çalışan oluşturuldu");
    }

    @Transactional(readOnly = true)
    public ApiResponse<List<EmployeeResponse>> getByCompany(UUID companyId) {
        List<EmployeeResponse> list = employeeRepository.findByCompanyId(companyId)
                .stream().map(this::toResponse).collect(Collectors.toList());
        return ApiResponse.success(list, "Çalışan listesi");
    }

    @Transactional(readOnly = true)
    public ApiResponse<List<EmployeeResponse>> getByDepartment(UUID departmentId) {
        List<EmployeeResponse> list = employeeRepository.findByDepartmentId(departmentId)
                .stream().map(this::toResponse).collect(Collectors.toList());
        return ApiResponse.success(list, "Departman çalışanları");
    }

    @Transactional(readOnly = true)
    public ApiResponse<EmployeeResponse> getById(UUID id) {
        return employeeRepository.findById(id)
                .map(e -> ApiResponse.success(toResponse(e), "Çalışan bulundu"))
                .orElse(ApiResponse.error("Çalışan bulunamadı"));
    }

    @Transactional(readOnly = true)
    public ApiResponse<List<EmployeeResponse>> search(UUID companyId, String query) {
        List<EmployeeResponse> list = employeeRepository.search(companyId, query)
                .stream().map(this::toResponse).collect(Collectors.toList());
        return ApiResponse.success(list, "Arama sonuçları");
    }

    @Transactional
    public ApiResponse<EmployeeResponse> update(UUID id, EmployeeRequest request) {
        return employeeRepository.findById(id).map(employee -> {
            employee.setDepartmentId(request.getDepartmentId());
            employee.setPositionId(request.getPositionId());
            employee.setFirstName(request.getFirstName());
            employee.setLastName(request.getLastName());
            employee.setBirthDate(request.getBirthDate());
            employee.setGender(request.getGender());
            employee.setPhone(request.getPhone());
            employee.setPersonalEmail(request.getPersonalEmail());
            employee.setWorkEmail(request.getWorkEmail());
            employee.setAddress(request.getAddress());
            employee.setCity(request.getCity());
            employee.setDistrict(request.getDistrict());
            employee.setStartDate(request.getStartDate());
            employee.setEndDate(request.getEndDate());
            if (request.getEmploymentType() != null) employee.setEmploymentType(request.getEmploymentType());
            if (request.getStatus() != null) employee.setStatus(request.getStatus());
            employee.setSalary(request.getSalary());
            if (request.getSalaryCurrency() != null) employee.setSalaryCurrency(request.getSalaryCurrency());
            employee.setEducationLevel(request.getEducationLevel());
            employee.setSchool(request.getSchool());
            employee.setSchoolDepartment(request.getSchoolDepartment());
            employee.setGraduationYear(request.getGraduationYear());
            employee.setEmergencyContactName(request.getEmergencyContactName());
            employee.setEmergencyContactPhone(request.getEmergencyContactPhone());
            employee.setEmergencyContactRelation(request.getEmergencyContactRelation());
            if (request.getAnnualLeaveDays() != null) employee.setAnnualLeaveDays(request.getAnnualLeaveDays());
            employee.setPerformanceNotes(request.getPerformanceNotes());
            employee.setGeneralNotes(request.getGeneralNotes());
            employee.setProfilePhotoUrl(request.getProfilePhotoUrl());

            return ApiResponse.success(toResponse(employeeRepository.save(employee)), "Çalışan güncellendi");
        }).orElse(ApiResponse.error("Çalışan bulunamadı"));
    }

    @Transactional
    public ApiResponse<EmployeeResponse> updateStatus(UUID id, EmployeeStatus status) {
        return employeeRepository.findById(id).map(employee -> {
            employee.setStatus(status);
            return ApiResponse.success(toResponse(employeeRepository.save(employee)), "Çalışan durumu güncellendi");
        }).orElse(ApiResponse.error("Çalışan bulunamadı"));
    }

    private EmployeeResponse toResponse(Employee e) {
        int remaining = (e.getAnnualLeaveDays() != null ? e.getAnnualLeaveDays() : 0)
                      - (e.getUsedLeaveDays() != null ? e.getUsedLeaveDays() : 0);

        return EmployeeResponse.builder()
                .id(e.getId())
                .companyId(e.getCompanyId())
                .departmentId(e.getDepartmentId())
                .positionId(e.getPositionId())
                .employeeNumber(e.getEmployeeNumber())
                .firstName(e.getFirstName())
                .lastName(e.getLastName())
                .fullName(e.getFirstName() + " " + e.getLastName())
                .nationalId(e.getNationalId())
                .birthDate(e.getBirthDate())
                .gender(e.getGender())
                .genderLabel(e.getGender() != null ? e.getGender().getLabel() : null)
                .phone(e.getPhone())
                .personalEmail(e.getPersonalEmail())
                .workEmail(e.getWorkEmail())
                .address(e.getAddress())
                .city(e.getCity())
                .district(e.getDistrict())
                .startDate(e.getStartDate())
                .endDate(e.getEndDate())
                .employmentType(e.getEmploymentType())
                .employmentTypeLabel(e.getEmploymentType() != null ? e.getEmploymentType().getLabel() : null)
                .status(e.getStatus())
                .statusLabel(e.getStatus() != null ? e.getStatus().getLabel() : null)
                .salary(e.getSalary())
                .salaryCurrency(e.getSalaryCurrency())
                .educationLevel(e.getEducationLevel())
                .educationLevelLabel(e.getEducationLevel() != null ? e.getEducationLevel().getLabel() : null)
                .school(e.getSchool())
                .schoolDepartment(e.getSchoolDepartment())
                .graduationYear(e.getGraduationYear())
                .emergencyContactName(e.getEmergencyContactName())
                .emergencyContactPhone(e.getEmergencyContactPhone())
                .emergencyContactRelation(e.getEmergencyContactRelation())
                .annualLeaveDays(e.getAnnualLeaveDays())
                .usedLeaveDays(e.getUsedLeaveDays())
                .remainingLeaveDays(remaining)
                .performanceNotes(e.getPerformanceNotes())
                .generalNotes(e.getGeneralNotes())
                .profilePhotoUrl(e.getProfilePhotoUrl())
                .createdAt(e.getCreatedAt())
                .updatedAt(e.getUpdatedAt())
                .build();
    }
}
