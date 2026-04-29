package com.sellspark.SellsHRMS.notification.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.sellspark.SellsHRMS.notification.dto.NotificationPreferenceDTO;
import com.sellspark.SellsHRMS.notification.entity.NotificationPreference;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface NotificationPreferenceMapper {

    // Entity → DTO
    @Mapping(target = "orgId", source = "organisation.id")
    @Mapping(target = "eventId", source = "notificationEvent.id")
    @Mapping(target = "eventCode", source = "notificationEvent.eventCode") // Flatten nested field
    NotificationPreferenceDTO toDto(NotificationPreference entity);

    // DTO → Entity (for create)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "organisation.id", source = "orgId")
    @Mapping(target = "organisation", ignore = true) // Don't map full object
    @Mapping(target = "notificationEvent.id", source = "eventId")
    @Mapping(target = "notificationEvent", ignore = true)
    NotificationPreference toEntity(NotificationPreferenceDTO dto);

    // Update existing entity
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "organisation", ignore = true)
    @Mapping(target = "notificationEvent", ignore = true)
    void updateEntityFromDto(NotificationPreferenceDTO dto, @MappingTarget NotificationPreference entity);
}
