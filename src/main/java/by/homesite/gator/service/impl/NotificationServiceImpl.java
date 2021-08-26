package by.homesite.gator.service.impl;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import by.homesite.gator.domain.Notification;
import by.homesite.gator.repository.NotificationRepository;
import by.homesite.gator.service.NotificationService;
import by.homesite.gator.service.dto.NotificationDTO;
import by.homesite.gator.service.mapper.NotificationMapper;

/**
 * Service Implementation for managing {@link Notification}.
 */
@Service
@Transactional
public class NotificationServiceImpl implements NotificationService
{

    private final Logger log = LoggerFactory.getLogger(NotificationServiceImpl.class);

    private final NotificationRepository notificationRepository;

    private final NotificationMapper notificationMapper;

    public NotificationServiceImpl(NotificationRepository notificationRepository, NotificationMapper notificationMapper) {
        this.notificationRepository = notificationRepository;
        this.notificationMapper = notificationMapper;
    }

    /**
     * Save a notification.
     *
     * @param notificationDTO the entity to save.
     * @return the persisted entity.
     */
    @Override
    public NotificationDTO save(NotificationDTO notificationDTO) {
        log.debug("Request to save Notification : {}", notificationDTO);
        Notification notification = notificationMapper.toEntity(notificationDTO);
        notificationRepository.save(notification);
        notificationDTO = notificationMapper.toDto(notification);
        return notificationDTO;
    }

    /**
     * Get all the notifications.
     *
     * @return the list of entities.
     */
    @Override
    @Transactional(readOnly = true)
    public List<NotificationDTO> findAll() {
        log.debug("Request to get all Notification");
        return notificationRepository.findAll().stream()
            .map(notificationMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }


    /**
     * Get one notifications by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<NotificationDTO> findOne(Long id) {
        log.debug("Request to get Notification : {}", id);
        return notificationRepository.findById(id)
            .map(notificationMapper::toDto);
    }

    /**
     * Delete the userProperties by id.
     *
     * @param id the id of the entity.
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete Notification : {}", id);
        notificationRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Notification findByName(String email)
    {
        log.debug("Request to get Notification by code : {}", email);
        return notificationRepository.findOneByName(email).orElse(null);
    }

}
