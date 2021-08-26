package by.homesite.gator.repository;

import java.nio.channels.FileChannel;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import by.homesite.gator.domain.UserNotifications;

/**
 * Spring Data  repository for the UserNotifications entity.
 */
@SuppressWarnings("unused")
@Repository
public interface UserNotificationsRepository extends JpaRepository<UserNotifications, Long> {

    List<UserNotifications> findByNotificationIdAndActive(Long id, Boolean active);
}
