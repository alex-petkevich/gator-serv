package by.homesite.gator.service.mapper;

import by.homesite.gator.domain.*;
import by.homesite.gator.service.dto.UserPropertiesDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link UserProperties} and its DTO {@link UserPropertiesDTO}.
 */
@Mapper(componentModel = "spring", uses = { UserMapper.class })
public interface UserPropertiesMapper extends EntityMapper<UserPropertiesDTO, UserProperties> {
    @Mapping(target = "user", source = "user", qualifiedByName = "login")
    UserPropertiesDTO toDto(UserProperties s);
}
