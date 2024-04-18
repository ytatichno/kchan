package com.maxdev.kchan.models.enums;

/**
 * Created by ytati
 * on 04.03.2024.
 */

public enum TopicStatus {
    CASUAL("Обычная"),
    NEW("Свежая"),
    PASSIVE("Пассивная"),
    ACTIVE("Активная"),
    MOVED("Переехала");

    private final String inRussian;

    TopicStatus(String inRussian) {
        this.inRussian = inRussian;
    }

    public String getInRussian() {
        return inRussian;
    }

    public static TopicStatus fromString(String text) {
        for (TopicStatus ts : TopicStatus.values()) {
            if (ts.inRussian.equalsIgnoreCase(text)) {
                return ts;
            }
        }
        return null;
    }
}
