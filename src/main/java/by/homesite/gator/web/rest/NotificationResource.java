package by.homesite.gator.web.rest;

import by.homesite.gator.service.NotificationService;
import by.homesite.gator.service.dto.NotificationDTO;
import by.homesite.gator.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
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
public class NotificationResource {

    private final Logger log = LoggerFactory.getLogger(NotificationResource.class);

    private static final String ENTITY_NAME = "notification";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final NotificationService notificationService;

    public NotificationResource(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    /**
     * {@code POST  /notification} : Create a new notification.
     *
     * @param notificationDTO the userPropertiesDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new userPropertiesDTO, or with status {@code 400 (Bad Request)} if the userProperties has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/notifications")
    public ResponseEntity<NotificationDTO> createNotification(@RequestBody NotificationDTO notificationDTO) throws URISyntaxException {
        log.debug("REST request to save notification : {}", notificationDTO);
        if (notificationDTO.getId() != null) {
            throw new BadRequestAlertException("A new notification cannot already have an ID", ENTITY_NAME, "idexists");
        }
        NotificationDTO result = notificationService.save(notificationDTO);
        return ResponseEntity
            .created(new URI("/api/notification/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /notification} : Updates an existing userProperties.
     *
     * @param notificationDTO the notificationDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated notificationDTO,
     * or with status {@code 400 (Bad Request)} if the notificationDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the notificationDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/notifications")
    public ResponseEntity<NotificationDTO> updateNotification(@RequestBody NotificationDTO notificationDTO) throws URISyntaxException {
        log.debug("REST request to update notification : {}", notificationDTO);
        if (notificationDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        NotificationDTO result = notificationService.save(notificationDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, notificationDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /notification} : get all the userProperties.
     *

     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of userProperties in body.
     */
    @GetMapping("/notifications")
    public List<NotificationDTO> getAllNotification() {
        log.debug("REST request to get all notifications");
        return notificationService.findAll();
    }

    /**
     * {@code GET  /notification/:id} : get the "id" notification.
     *
     * @param id the id of the notificationDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the notificationDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/notifications/{id}")
    public ResponseEntity<NotificationDTO> getNotification(@PathVariable Long id) {
        log.debug("REST request to get UserProperties : {}", id);
        Optional<NotificationDTO> notificationDTO = notificationService.findOne(id);
        return ResponseUtil.wrapOrNotFound(notificationDTO);
    }

    /**
     * {@code DELETE  /notification/:id} : delete the "id" notification.
     *
     * @param id the id of the notificationDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/notifications/{id}")
    public ResponseEntity<Void> deleteNotification(@PathVariable Long id) {
        log.debug("REST request to delete notification : {}", id);
        notificationService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
