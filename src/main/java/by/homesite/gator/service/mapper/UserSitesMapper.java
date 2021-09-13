package by.homesite.gator.service.mapper;

import by.homesite.gator.domain.*;
import by.homesite.gator.service.dto.UserSitesDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link UserSites} and its DTO {@link UserSitesDTO}.
 */
@Mapper(componentModel = "spring", uses = { UserMapper.class })
public interface UserSitesMapper extends EntityMapper<UserSitesDTO, UserSites> {
    @Mapping(target = "user", source = "user", qualifiedByName = "login")
    UserSitesDTO toDto(UserSites s);
}
