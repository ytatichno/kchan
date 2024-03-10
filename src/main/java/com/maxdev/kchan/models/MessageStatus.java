package com.maxdev.kchan.models;

/**
 * Created by ytati
 * on 04.03.2024.
 */
public enum MessageStatus {
    CASUAL("Обычное"),
    VIP("VIP"),
    BANNED("Забаненное"),
    NOTIFICATION("Уведомление");

    private final String inRussian;

    MessageStatus(String inRussian) {
        this.inRussian = inRussian;
    }

    public String getInRussian() {
        return inRussian;
    }
}
