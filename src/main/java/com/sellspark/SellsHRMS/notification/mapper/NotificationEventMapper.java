package com.sellspark.SellsHRMS.notification.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.sellspark.SellsHRMS.notification.dto.NotificationEventDTO;
import com.sellspark.SellsHRMS.notification.entity.NotificationEvent;

@Mapper(componentModel = "spring")
public interface NotificationEventMapper {
    NotificationEventDTO toDto(NotificationEvent entity);

    @Mapping(target = "id", ignore = true) // Don't allow client to set ID on create
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    NotificationEvent toEntity(NotificationEventDTO dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromDto(NotificationEventDTO dto, @MappingTarget NotificationEvent entity);
}