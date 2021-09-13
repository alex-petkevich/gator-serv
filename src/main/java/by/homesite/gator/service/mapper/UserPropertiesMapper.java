package by.homesite.gator.service.mapper;

import by.homesite.gator.domain.*;
import by.homesite.gator.service.dto.UserPropertiesDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity {@link UserProperties} and its DTO {@link UserPropertiesDTO}.
 */
@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface UserPropertiesMapper extends EntityMapper<UserPropertiesDTO, UserProperties> {

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "user.login", target = "userLogin")
    UserPropertiesDTO toDto(UserProperties userProperties);

    @Mapping(source = "userId", target = "user")
    UserProperties toEntity(UserPropertiesDTO userPropertiesDTO);

    default UserProperties fromId(Long id) {
        if (id == null) {
            return null;
        }
        UserProperties userProperties = new UserProperties();
        userProperties.setId(id);
        return userProperties;
    }
}
