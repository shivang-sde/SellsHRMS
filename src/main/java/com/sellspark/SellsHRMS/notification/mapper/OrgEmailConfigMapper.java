package com.sellspark.SellsHRMS.notification.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.sellspark.SellsHRMS.notification.dto.EmailConfigDTO;
import com.sellspark.SellsHRMS.notification.entity.OrgEmailConfig;

@Mapper(componentModel = "spring")
public interface OrgEmailConfigMapper {

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "orgId", ignore = true)
    @Mapping(target = "organisation", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromDto(EmailConfigDTO dto, @MappingTarget OrgEmailConfig entity);

    @Mapping(target = "orgId", source = "organisation.id")
    @Mapping(target = "smtpPassword", ignore = true) // Never expose password in responses
    @Mapping(target = "sentToday", ignore = true) // Internal counters
    @Mapping(target = "sentThisHour", ignore = true)
    @Mapping(target = "lastResetAt", ignore = true)
    @Mapping(target = "createdAt", ignore = true) // Optional: include if needed
    @Mapping(target = "updatedAt", ignore = true)
    EmailConfigDTO toDto(OrgEmailConfig entity);
}
