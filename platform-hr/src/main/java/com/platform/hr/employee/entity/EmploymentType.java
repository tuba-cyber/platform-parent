package com.platform.hr.employee.entity;

public enum EmploymentType {
    TAM_ZAMANLI("Tam Zamanlı"),
    YARI_ZAMANLI("Yarı Zamanlı"),
    SOZLESMELI("Sözleşmeli"),
    STAJYER("Stajyer"),
    PART_TIME("Part-Time"),
    REMOTE("Uzaktan");

    private final String label;

    EmploymentType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
