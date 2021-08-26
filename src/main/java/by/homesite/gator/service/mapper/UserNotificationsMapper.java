package by.homesite.gator.service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import by.homesite.gator.domain.UserNotifications;
import by.homesite.gator.domain.UserSearches;
import by.homesite.gator.service.dto.UserNotificationsDTO;

/**
 * Mapper for the entity {@link UserNotifications} and its DTO {@link UserNotificationsDTO}.
 */
@Mapper(componentModel = "spring", uses = {NotificationMapper.class, UserMapper.class, UserSearchesMapper.class })
public interface UserNotificationsMapper extends EntityMapper<UserNotificationsDTO, UserNotifications> {

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "notification.id", target = "notificationId")
    @Mapping(source = "userSearches.id", target = "userSearchesId", ignore = true)
    UserNotificationsDTO toDto(UserNotifications userNotifications);

    @Mapping(source = "userId", target = "user")
    @Mapping(source = "notificationId", target = "notification")
    @Mapping(source = "userSearchesId", target = "userSearches", ignore = true)
    UserNotifications toEntity(UserNotificationsDTO userNotificationsDTO);

    default UserNotifications fromId(Long id) {
        if (id == null) {
            return null;
        }
        UserNotifications userNotifications = new UserNotifications();
        userNotifications.setId(id);
        return userNotifications;
    }
}
