package com.maxdev.kchan.models.enums;

import jakarta.persistence.AttributeConverter;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by ytati
 * on 05.04.2024.
 */
@Slf4j
public class TopicStatusConverter implements AttributeConverter<TopicStatus, String> {

    @Override
    public String convertToDatabaseColumn(TopicStatus attribute) {
        return attribute.getInRussian();
    }

    @Override
    public TopicStatus convertToEntityAttribute(String dbData) {
        return TopicStatus.fromString(dbData);
    }
}
