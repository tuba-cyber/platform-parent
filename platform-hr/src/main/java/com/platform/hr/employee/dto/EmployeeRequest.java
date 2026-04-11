package com.platform.hr.employee.dto;

import com.platform.hr.employee.entity.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
public class EmployeeRequest {

    // Organizasyon
    @NotNull(message = "Şirket ID zorunludur")
    private UUID companyId;
    private UUID departmentId;
    private UUID positionId;
    private String employeeNumber;

    // Kişisel
    @NotBlank(message = "Ad zorunludur")
    private String firstName;

    @NotBlank(message = "Soyad zorunludur")
    private String lastName;

    private String nationalId;
    private LocalDate birthDate;
    private Gender gender;
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
    private EmployeeStatus status;
    private BigDecimal salary;
    private String salaryCurrency;

    // Eğitim
    private EducationLevel educationLevel;
    private String school;
    private String schoolDepartment;
    private Integer graduationYear;

    // Acil durum
    private String emergencyContactName;
    private String emergencyContactPhone;
    private String emergencyContactRelation;

    // Diğer
    private Integer annualLeaveDays;
    private String performanceNotes;
    private String generalNotes;
    private String profilePhotoUrl;
}
