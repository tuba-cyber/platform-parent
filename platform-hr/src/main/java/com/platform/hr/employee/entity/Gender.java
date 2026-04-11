package com.platform.hr.employee.entity;

public enum Gender {
    ERKEK("Erkek"),
    KADIN("Kadın"),
    BELIRTILMEMIS("Belirtilmemiş");

    private final String label;

    Gender(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
