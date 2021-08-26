package by.homesite.gator.service.mapper;

import org.mapstruct.Mapper;

import by.homesite.gator.domain.Notification;
import by.homesite.gator.service.dto.NotificationDTO;

/**
 * Mapper for the entity {@link Notification} and its DTO {@link NotificationDTO}.
 */
@Mapper(componentModel = "spring", uses = {UserNotificationsMapper.class})
public interface NotificationMapper extends EntityMapper<NotificationDTO, Notification> {

    NotificationDTO toDto(Notification notification);

    Notification toEntity(NotificationDTO notificationDTO);

    default Notification fromId(Long id) {
        if (id == null) {
            return null;
        }
        Notification notification = new Notification();
        notification.setId(id);
        return notification;
    }
}
