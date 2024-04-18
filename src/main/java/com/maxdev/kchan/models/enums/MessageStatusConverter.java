package com.maxdev.kchan.models.enums;

import jakarta.persistence.AttributeConverter;

/**
 * Created by ytati
 * on 05.04.2024.
 */
public class MessageStatusConverter implements AttributeConverter<MessageStatus, String> {

    @Override
    public String convertToDatabaseColumn(MessageStatus attribute) {
        return attribute.getInRussian();
    }

    @Override
    public MessageStatus convertToEntityAttribute(String dbData) {
        return MessageStatus.fromString(dbData);
    }
}
