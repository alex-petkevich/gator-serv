package by.homesite.gator.service.impl;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import by.homesite.gator.domain.Notification;
import by.homesite.gator.domain.UserNotifications;
import by.homesite.gator.repository.UserNotificationsRepository;
import by.homesite.gator.service.UserNotificationsService;
import by.homesite.gator.service.UserSearchesService;
import by.homesite.gator.service.dto.ItemDTO;
import by.homesite.gator.service.dto.UserNotificationsDTO;
import by.homesite.gator.service.mapper.UserNotificationsMapper;

/**
 * Service Implementation for managing {@link Notification}.
 */
@Service
@Transactional
public class UserNotificationsServiceImpl implements UserNotificationsService
{

    private final Logger log = LoggerFactory.getLogger(UserNotificationsServiceImpl.class);

    private final UserNotificationsRepository userNotificationsRepository;

    private final UserNotificationsMapper userNotificationsMapper;

    private final UserSearchesService userSearchesService;

    public UserNotificationsServiceImpl(UserNotificationsRepository userNotificationsRepository, UserNotificationsMapper userNotificationsMapper, UserSearchesService userSearchesService) {
        this.userNotificationsRepository = userNotificationsRepository;
        this.userNotificationsMapper = userNotificationsMapper;
        this.userSearchesService = userSearchesService;
    }

    /**
     * Save a user notification.
     *
     * @param userNotificationsDTO the entity to save.
     * @return the persisted entity.
     */
    @Override
    public UserNotificationsDTO save(UserNotificationsDTO userNotificationsDTO) {
        log.debug("Request to save User Notification : {}", userNotificationsDTO);
        UserNotifications notification = userNotificationsMapper.toEntity(userNotificationsDTO);
        userNotificationsRepository.save(notification);
        userNotificationsDTO = userNotificationsMapper.toDto(notification);
        return userNotificationsDTO;
    }

    /**
     * Get all the user notifications.
     *
     * @return the list of entities.
     */
    @Override
    @Transactional(readOnly = true)
    public List<UserNotificationsDTO> findAll() {
        log.debug("Request to get all User Notification");
        return userNotificationsRepository.findAll().stream()
            .map(userNotificationsMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }


    /**
     * Get one user notification by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<UserNotificationsDTO> findOne(Long id) {
        log.debug("Request to get User Notification : {}", id);
        return userNotificationsRepository.findById(id)
            .map(userNotificationsMapper::toDto);
    }

    /**
     * Delete the user notification by id.
     *
     * @param id the id of the entity.
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete User Notification : {}", id);
        userNotificationsRepository.deleteById(id);
    }

    @Override
    public List<UserNotifications> findUsersForNotifications(Long id, ItemDTO item)
    {
        log.debug("Request to list Notifications by user : {}", id);
        List<UserNotifications> result = new ArrayList<>();
        List<UserNotifications> userForNotifications = new LinkedList<>(userNotificationsRepository.findByNotificationIdAndIsActive(id, true));
        userForNotifications.forEach(userNotifications -> {
            if (userNotifications.getUserSearches() == null ||
                    userSearchesService.checkIfItemEligible(userNotifications.getUserSearches(), item)) {
                result.add(userNotifications);
            }
        });

        return result;
    }

    @Override
    public List<UserNotifications> findUsersNotificationsForSearch(Long userId, Long notificationId, Long userSearchesId)
    {
        return userNotificationsRepository.findByUserIdAndNotificationIdAndUserSearchesId(userId, notificationId, userSearchesId);
    }

    @Override
    public void calculate(UserNotifications userNotifications)
    {
        userNotifications.setTotalQty(userNotifications.getTotalQty() + 1);
        userNotifications.setLastSent(ZonedDateTime.now());
        userNotificationsRepository.save(userNotifications);
    }

}
