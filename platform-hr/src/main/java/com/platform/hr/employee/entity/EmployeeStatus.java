package com.platform.hr.employee.entity;

public enum EmployeeStatus {
    AKTIF("Aktif"),
    PASIF("Pasif"),
    IZINDE("İzinde"),
    ISTIFA("İstifa"),
    ISTEN_CIKARILDI("İşten Çıkarıldı"),
    EMEKLI("Emekli");

    private final String label;

    EmployeeStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
