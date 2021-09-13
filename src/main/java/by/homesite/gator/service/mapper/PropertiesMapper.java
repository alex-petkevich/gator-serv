package by.homesite.gator.service.mapper;

import by.homesite.gator.domain.*;
import by.homesite.gator.service.dto.PropertiesDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity {@link Properties} and its DTO {@link PropertiesDTO}.
 */
@Mapper(componentModel = "spring", uses = {})
public interface PropertiesMapper extends EntityMapper<PropertiesDTO, Properties> {



    default Properties fromId(Long id) {
        if (id == null) {
            return null;
        }
        Properties properties = new Properties();
        properties.setId(id);
        return properties;
    }
}
