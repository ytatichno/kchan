package com.maxdev.kchan.models;

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
}
