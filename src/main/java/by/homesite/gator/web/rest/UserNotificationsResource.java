package by.homesite.gator.web.rest;

import by.homesite.gator.repository.UserRepository;
import by.homesite.gator.security.SecurityUtils;
import by.homesite.gator.service.NotificationService;
import by.homesite.gator.service.UserNotificationsService;
import by.homesite.gator.service.UserSearchesService;
import by.homesite.gator.service.dto.UserNotificationsDTO;
import by.homesite.gator.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link by.homesite.gator.domain.Notification}.
 */
@RestController
@RequestMapping("/api")
public class UserNotificationsResource {

    private final Logger log = LoggerFactory.getLogger(UserNotificationsResource.class);

    private static final String ENTITY_NAME = "userNotifications";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final UserNotificationsService userNotificationsService;

    private final UserSearchesService userSearchesService;
    private final NotificationService notificationService;
    private final UserRepository userRepository;

    public UserNotificationsResource(
        UserNotificationsService userNotificationsService,
        UserRepository userRepository,
        UserSearchesService userSearchesService,
        NotificationService notificationService
    ) {
        this.userNotificationsService = userNotificationsService;
        this.userSearchesService = userSearchesService;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
    }

    /**
     * {@code POST  /user-notifications} : Create a new notification.
     *
     * @param userNotificationsDTO the userNotificationsDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new userPropertiesDTO, or with status {@code 400 (Bad Request)} if the userNotifications has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/user-notifications")
    public ResponseEntity<UserNotificationsDTO> createUserNotifications(@RequestBody UserNotificationsDTO userNotificationsDTO)
        throws URISyntaxException {
        log.debug("REST request to save notification : {}", userNotificationsDTO);
        if (userNotificationsDTO.getId() != null) {
            throw new BadRequestAlertException("A new notification cannot already have an ID", ENTITY_NAME, "idexists");
        }

        userNotificationsDTO.setNotificationId(notificationService.findByName(userNotificationsDTO.getNotificationName()).getId());
        if (userNotificationsDTO.getNotificationId() == null) {
            throw new BadRequestAlertException("Notification is not exists", ENTITY_NAME, "notificationnotexists");
        }

        if (!SecurityUtils.isAuthenticated()) {
            throw new BadRequestAlertException("User not logged in", ENTITY_NAME, "loginrequired");
        }

        Long userId = userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin().get()).get().getId();
        userNotificationsDTO.setUserId(userId);

        if (
            userNotificationsService
                .findUsersNotificationsForSearch(userId, userNotificationsDTO.getNotificationId(), userNotificationsDTO.getUserSearchesId())
                .size() >
            0
        ) {
            throw new BadRequestAlertException("Notification for this filter already exists", ENTITY_NAME, "nameexists");
        }
        userNotificationsDTO.setIsActive(true);
        userNotificationsDTO.setTotalQty(0L);
        userNotificationsDTO.setLastSent(ZonedDateTime.now());

        UserNotificationsDTO result = userNotificationsService.save(userNotificationsDTO);
        return ResponseEntity
            .created(new URI("/api/user-notifications/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /user-notifications} : Updates an existing userProperties.
     *
     * @param userNotificationsDTO the userNotificationsDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated userNotificationsDTO,
     * or with status {@code 400 (Bad Request)} if the userNotificationsDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the userNotificationsDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/user-notifications")
    public ResponseEntity<UserNotificationsDTO> updateUserNotifications(@RequestBody UserNotificationsDTO userNotificationsDTO)
        throws URISyntaxException {
        log.debug("REST request to update user notifications : {}", userNotificationsDTO);
        if (userNotificationsDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        UserNotificationsDTO result = userNotificationsService.save(userNotificationsDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, userNotificationsDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /user-notifications} : get all the userProperties.
     *

     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of userProperties in body.
     */
    @GetMapping("/user-notifications")
    public List<UserNotificationsDTO> getAllUserNotifications() {
        log.debug("REST request to get all user notifications");
        return userNotificationsService.findAll();
    }

    /**
     * {@code GET  /user-notifications/:id} : get the "id" notification.
     *
     * @param id the id of the UserNotificationsDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the UserNotificationsDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/user-notifications/{id}")
    public ResponseEntity<UserNotificationsDTO> getUserNotifications(@PathVariable Long id) {
        log.debug("REST request to get User Notifications : {}", id);
        Optional<UserNotificationsDTO> userNotificationsDTO = userNotificationsService.findOne(id);
        return ResponseUtil.wrapOrNotFound(userNotificationsDTO);
    }

    /**
     * {@code DELETE  /user-notifications/:id} : delete the "id" notification.
     *
     * @param id the id of the UserNotificationsDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/user-notifications/{id}")
    public ResponseEntity<Void> deleteUserNotifications(@PathVariable Long id) {
        log.debug("REST request to delete User Notifications : {}", id);
        userNotificationsService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
