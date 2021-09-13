package by.homesite.gator.service.mapper;

import by.homesite.gator.domain.*;
import by.homesite.gator.service.dto.UserSitesDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity {@link UserSites} and its DTO {@link UserSitesDTO}.
 */
@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface UserSitesMapper extends EntityMapper<UserSitesDTO, UserSites> {

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "user.login", target = "userLogin")
    UserSitesDTO toDto(UserSites userSites);

    @Mapping(source = "userId", target = "user")
    UserSites toEntity(UserSitesDTO userSitesDTO);

    default UserSites fromId(Long id) {
        if (id == null) {
            return null;
        }
        UserSites userSites = new UserSites();
        userSites.setId(id);
        return userSites;
    }
}
