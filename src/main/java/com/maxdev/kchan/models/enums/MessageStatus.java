package com.maxdev.kchan.models.enums;

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

    public static MessageStatus fromString(String text) {
        for (MessageStatus ms : MessageStatus.values()) {
            if (ms.inRussian.equalsIgnoreCase(text)) {
                return ms;
            }
        }
        return null;
    }
}
