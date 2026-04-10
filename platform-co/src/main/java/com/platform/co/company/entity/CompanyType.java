package com.platform.co.company.entity;

public enum CompanyType {
    AS("Anonim Şirket"),
    LTD("Limited Şirket"),
    SAHIS("Şahıs Şirketi"),
    KAMU("Kamu Kurumu"),
    BELEDIYE("Belediye"),
    UNIVERSITE("Üniversite"),
    DERNEKvakif("Dernek / Vakıf"),
    DIGER("Diğer");

    private final String label;

    CompanyType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
