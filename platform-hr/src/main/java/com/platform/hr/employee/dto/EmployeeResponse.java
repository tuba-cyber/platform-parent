package com.platform.hr.employee.dto;

import com.platform.hr.employee.entity.*;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class EmployeeResponse {
    // Organizasyon
    private UUID id;
    private UUID companyId;
    private UUID departmentId;
    private UUID positionId;
    private String employeeNumber;

    // Kişisel
    private String firstName;
    private String lastName;
    private String fullName;
    private String nationalId;
    private LocalDate birthDate;
    private Gender gender;
    private String genderLabel;
    private String phone;
    private String personalEmail;
    private String workEmail;
    private String address;
    private String city;
    private String district;

    // İş
    private LocalDate startDate;
    private LocalDate endDate;
    private EmploymentType employmentType;
    private String employmentTypeLabel;
    private EmployeeStatus status;
    private String statusLabel;
    private BigDecimal salary;
    private String salaryCurrency;

    // Eğitim
    private EducationLevel educationLevel;
    private String educationLevelLabel;
    private String school;
    private String schoolDepartment;
    private Integer graduationYear;

    // Acil durum
    private String emergencyContactName;
    private String emergencyContactPhone;
    private String emergencyContactRelation;

    // İzin
    private Integer annualLeaveDays;
    private Integer usedLeaveDays;
    private Integer remainingLeaveDays;

    // Diğer
    private String performanceNotes;
    private String generalNotes;
    private String profilePhotoUrl;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
