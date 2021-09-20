package by.homesite.gator.web.rest;

import by.homesite.gator.repository.UserPropertiesRepository;
import by.homesite.gator.service.UserPropertiesService;
import by.homesite.gator.service.dto.UserPropertiesDTO;
import by.homesite.gator.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link by.homesite.gator.domain.UserProperties}.
 */
@RestController
@RequestMapping("/api")
public class UserPropertiesResource {

    private final Logger log = LoggerFactory.getLogger(UserPropertiesResource.class);

    private static final String ENTITY_NAME = "userProperties";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final UserPropertiesService userPropertiesService;

    private final UserPropertiesRepository userPropertiesRepository;

    public UserPropertiesResource(UserPropertiesService userPropertiesService, UserPropertiesRepository userPropertiesRepository) {
        this.userPropertiesService = userPropertiesService;
        this.userPropertiesRepository = userPropertiesRepository;
    }

    /**
     * {@code POST  /user-properties} : Create a new userProperties.
     *
     * @param userPropertiesDTO the userPropertiesDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new userPropertiesDTO, or with status {@code 400 (Bad Request)} if the userProperties has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/user-properties")
    public ResponseEntity<UserPropertiesDTO> createUserProperties(@RequestBody UserPropertiesDTO userPropertiesDTO)
        throws URISyntaxException {
        log.debug("REST request to save UserProperties : {}", userPropertiesDTO);
        if (userPropertiesDTO.getId() != null) {
            throw new BadRequestAlertException("A new userProperties cannot already have an ID", ENTITY_NAME, "idexists");
        }
        UserPropertiesDTO result = userPropertiesService.save(userPropertiesDTO);
        return ResponseEntity
            .created(new URI("/api/user-properties/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /user-properties/:id} : Updates an existing userProperties.
     *
     * @param id the id of the userPropertiesDTO to save.
     * @param userPropertiesDTO the userPropertiesDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated userPropertiesDTO,
     * or with status {@code 400 (Bad Request)} if the userPropertiesDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the userPropertiesDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/user-properties/{id}")
    public ResponseEntity<UserPropertiesDTO> updateUserProperties(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody UserPropertiesDTO userPropertiesDTO
    ) throws URISyntaxException {
        log.debug("REST request to update UserProperties : {}, {}", id, userPropertiesDTO);
        if (userPropertiesDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, userPropertiesDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!userPropertiesRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        UserPropertiesDTO result = userPropertiesService.save(userPropertiesDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, userPropertiesDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /user-properties/:id} : Partial updates given fields of an existing userProperties, field will ignore if it is null
     *
     * @param id the id of the userPropertiesDTO to save.
     * @param userPropertiesDTO the userPropertiesDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated userPropertiesDTO,
     * or with status {@code 400 (Bad Request)} if the userPropertiesDTO is not valid,
     * or with status {@code 404 (Not Found)} if the userPropertiesDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the userPropertiesDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/user-properties/{id}", consumes = "application/merge-patch+json")
    public ResponseEntity<UserPropertiesDTO> partialUpdateUserProperties(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody UserPropertiesDTO userPropertiesDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update UserProperties partially : {}, {}", id, userPropertiesDTO);
        if (userPropertiesDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, userPropertiesDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!userPropertiesRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<UserPropertiesDTO> result = userPropertiesService.partialUpdate(userPropertiesDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, userPropertiesDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /user-properties} : get all the userProperties.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of userProperties in body.
     */
    @GetMapping("/user-properties")
    public List<UserPropertiesDTO> getAllUserProperties() {
        log.debug("REST request to get all UserProperties");
        return userPropertiesService.findAll();
    }

    /**
     * {@code GET  /user-properties/:id} : get the "id" userProperties.
     *
     * @param id the id of the userPropertiesDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the userPropertiesDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/user-properties/{id}")
    public ResponseEntity<UserPropertiesDTO> getUserProperties(@PathVariable Long id) {
        log.debug("REST request to get UserProperties : {}", id);
        Optional<UserPropertiesDTO> userPropertiesDTO = userPropertiesService.findOne(id);
        return ResponseUtil.wrapOrNotFound(userPropertiesDTO);
    }

    /**
     * {@code DELETE  /user-properties/:id} : delete the "id" userProperties.
     *
     * @param id the id of the userPropertiesDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/user-properties/{id}")
    public ResponseEntity<Void> deleteUserProperties(@PathVariable Long id) {
        log.debug("REST request to delete UserProperties : {}", id);
        userPropertiesService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /_search/user-properties?query=:query} : search for the userProperties corresponding
     * to the query.
     *
     * @param query the query of the userProperties search.
     * @return the result of the search.
     */
    @GetMapping("/_search/user-properties")
    public List<UserPropertiesDTO> searchUserProperties(@RequestParam String query) {
        log.debug("REST request to search UserProperties for query {}", query);
        return userPropertiesService.search(query);
    }
}
