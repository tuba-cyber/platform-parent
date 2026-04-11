package com.platform.hr.employee.entity;

public enum EducationLevel {
    ILKOKUL("İlkokul"),
    ORTAOKUL("Ortaokul"),
    LISE("Lise"),
    ON_LISANS("Ön Lisans"),
    LISANS("Lisans"),
    YUKSEK_LISANS("Yüksek Lisans"),
    DOKTORA("Doktora");

    private final String label;

    EducationLevel(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
