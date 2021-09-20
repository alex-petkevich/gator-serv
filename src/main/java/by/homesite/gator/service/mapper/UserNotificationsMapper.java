package by.homesite.gator.service.mapper;

import by.homesite.gator.domain.UserNotifications;
import by.homesite.gator.service.dto.UserNotificationsDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper for the entity {@link UserNotifications} and its DTO {@link UserNotificationsDTO}.
 */
@Mapper(componentModel = "spring", uses = { NotificationMapper.class, UserMapper.class, UserSearchesMapper.class })
public interface UserNotificationsMapper extends EntityMapper<UserNotificationsDTO, UserNotifications> {
    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "notification.id", target = "notificationId")
    @Mapping(source = "notification.name", target = "notificationName")
    @Mapping(source = "userSearches.id", target = "userSearchesId")
    @Mapping(source = "userSearches.name", target = "userSearchesName")
    UserNotificationsDTO toDto(UserNotifications userNotifications);

    @Mapping(source = "userId", target = "user")
    @Mapping(source = "notificationId", target = "notification.id")
    @Mapping(source = "userSearchesId", target = "userSearches.id")
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
