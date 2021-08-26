package by.homesite.gator.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import by.homesite.gator.domain.Notification;
import by.homesite.gator.domain.Rate;
import by.homesite.gator.service.dto.NotificationDTO;

/**
 * Spring Data  repository for the Notification entity.
 */
@SuppressWarnings("unused")
@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    Optional<Notification> findOneByName(String email);
}
