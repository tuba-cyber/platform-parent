package com.platform.moduleengine.navigation.entity;

public enum NavigationType {
    MODULE,     // Bir modüle gider
    SCREEN,     // Belirli bir ekrana gider
    EXTERNAL,   // Dış URL'e gider
    GROUP       // Alt menüleri olan grup (tıklanamaz)
}