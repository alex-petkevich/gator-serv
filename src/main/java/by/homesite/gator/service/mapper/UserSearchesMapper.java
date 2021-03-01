package by.homesite.gator.service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import by.homesite.gator.domain.UserSearches;
import by.homesite.gator.service.dto.UserSearchesDTO;

/**
 * Mapper for the entity {@link UserSearches} and its DTO {@link UserSearchesDTO}.
 */
@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface UserSearchesMapper extends EntityMapper<UserSearchesDTO, UserSearches> {

    @Mapping(source = "user.id", target = "userId")
    UserSearchesDTO toDto(UserSearches userSearches);

    @Mapping(source = "userId", target = "user")
    UserSearches toEntity(UserSearchesDTO userSearchesDTO);

    default UserSearches fromId(Long id) {
        if (id == null) {
            return null;
        }
        UserSearches userSearches = new UserSearches();
        userSearches.setId(id);
        return userSearches;
    }
}
