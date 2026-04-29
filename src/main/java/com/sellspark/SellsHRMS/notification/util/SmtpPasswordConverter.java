package com.sellspark.SellsHRMS.notification.util;

import org.springframework.stereotype.Component;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.RequiredArgsConstructor;

@Component
@Converter
@RequiredArgsConstructor
public class SmtpPasswordConverter implements AttributeConverter<String, String> {

    private final AesEncryptionUtil aesEncryptionUtil;

    @Override
    public String convertToDatabaseColumn(String attribute) {
        if (attribute == null)
            return null;
        return aesEncryptionUtil.encrypt(attribute);
    }

    @Override
    public String convertToEntityAttribute(String dbData) {
        if (dbData == null)
            return null;
        return aesEncryptionUtil.decrypt(dbData);
    }
}
