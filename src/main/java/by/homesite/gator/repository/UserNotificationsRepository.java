package by.homesite.gator.repository;

import by.homesite.gator.domain.UserNotifications;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data  repository for the UserNotifications entity.
 */
@SuppressWarnings("unused")
@Repository
public interface UserNotificationsRepository extends JpaRepository<UserNotifications, Long> {
    List<UserNotifications> findByNotificationIdAndIsActive(Long id, Boolean active);

    List<UserNotifications> findByUserIdAndNotificationIdAndUserSearchesId(Long userId, Long notificationId, Long userSearchesId);
}
