package by.homesite.gator.service;

import java.util.List;
import java.util.Optional;

import by.homesite.gator.domain.UserNotifications;
import by.homesite.gator.service.dto.ItemDTO;
import by.homesite.gator.service.dto.UserNotificationsDTO;

/**
 * Service Interface for managing {@link by.homesite.gator.domain.UserNotifications}.
 */
public interface UserNotificationsService
{

    /**
     * Save a userNotifications.
     *
     * @param userNotificationsDTO the entity to save.
     * @return the persisted entity.
     */
    UserNotificationsDTO save(UserNotificationsDTO userNotificationsDTO);

    /**
     * Get all the userNotifications.
     *
     * @return the list of entities.
     */
    List<UserNotificationsDTO> findAll();


    /**
     * Get the "id" userNotifications.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<UserNotificationsDTO> findOne(Long id);

    /**
     * Delete the "id" userNotifications.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    List<UserNotifications> findUsersForNotifications(Long id, ItemDTO item);

    List<UserNotifications> findUsersNotificationsForSearch(Long userId, Long notificationId, Long userSearchesId);

	void calculate(UserNotifications userNotifications);
}
