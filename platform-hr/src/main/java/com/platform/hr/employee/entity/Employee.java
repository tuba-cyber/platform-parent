package com.platform.hr.employee.entity;

import com.platform.core.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "hr_employees")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Employee extends BaseEntity {

    // ─── Şirket / Organizasyon ───────────────────────────────────────────────
    @Column(nullable = false)
    private UUID companyId;

    @Column
    private UUID departmentId;

    @Column
    private UUID positionId;

    /** Çalışan sicil numarası */
    @Column(length = 50, unique = true)
    private String employeeNumber;

    // ─── Kişisel Bilgiler ────────────────────────────────────────────────────
    @Column(nullable = false, length = 100)
    private String firstName;

    @Column(nullable = false, length = 100)
    private String lastName;

    @Column(length = 11, unique = true)
    private String nationalId;

    @Column
    private LocalDate birthDate;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private Gender gender;

    @Column(length = 20)
    private String phone;

    @Column(length = 150)
    private String personalEmail;

    /** Şirket e-posta adresi */
    @Column(length = 150)
    private String workEmail;

    @Column(length = 500)
    private String address;

    @Column(length = 100)
    private String city;

    @Column(length = 100)
    private String district;

    // ─── İş Bilgileri ────────────────────────────────────────────────────────
    @Column
    private LocalDate startDate;

    @Column
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    @Column(length = 30)
    @Builder.Default
    private EmploymentType employmentType = EmploymentType.TAM_ZAMANLI;

    @Enumerated(EnumType.STRING)
    @Column(length = 30)
    @Builder.Default
    private EmployeeStatus status = EmployeeStatus.AKTIF;

    /** Maaş (opsiyonel) */
    @Column(precision = 12, scale = 2)
    private BigDecimal salary;

    @Column(length = 10)
    @Builder.Default
    private String salaryCurrency = "TRY";

    // ─── Eğitim ──────────────────────────────────────────────────────────────
    @Enumerated(EnumType.STRING)
    @Column(length = 30)
    private EducationLevel educationLevel;

    @Column(length = 200)
    private String school;

    @Column(length = 150)
    private String schoolDepartment;

    @Column
    private Integer graduationYear;

    // ─── Acil Durum Kişisi ───────────────────────────────────────────────────
    @Column(length = 150)
    private String emergencyContactName;

    @Column(length = 20)
    private String emergencyContactPhone;

    @Column(length = 100)
    private String emergencyContactRelation;

    // ─── İzin & Notlar ───────────────────────────────────────────────────────
    /** Yıllık izin hakkı (gün) */
    @Column
    @Builder.Default
    private Integer annualLeaveDays = 14;

    /** Kullanılan izin (gün) */
    @Column
    @Builder.Default
    private Integer usedLeaveDays = 0;

    @Column(columnDefinition = "TEXT")
    private String performanceNotes;

    @Column(columnDefinition = "TEXT")
    private String generalNotes;

    // ─── Profil fotoğrafı ────────────────────────────────────────────────────
    @Column(length = 500)
    private String profilePhotoUrl;
}
