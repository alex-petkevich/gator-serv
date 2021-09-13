package by.homesite.gator.web.rest;

import static org.elasticsearch.index.query.QueryBuilders.*;

import by.homesite.gator.repository.UserSitesRepository;
import by.homesite.gator.service.UserSitesService;
import by.homesite.gator.service.dto.UserSitesDTO;
import by.homesite.gator.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.StreamSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link by.homesite.gator.domain.UserSites}.
 */
@RestController
@RequestMapping("/api")
public class UserSitesResource {

    private final Logger log = LoggerFactory.getLogger(UserSitesResource.class);

    private static final String ENTITY_NAME = "userSites";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final UserSitesService userSitesService;

    private final UserSitesRepository userSitesRepository;

    public UserSitesResource(UserSitesService userSitesService, UserSitesRepository userSitesRepository) {
        this.userSitesService = userSitesService;
        this.userSitesRepository = userSitesRepository;
    }

    /**
     * {@code POST  /user-sites} : Create a new userSites.
     *
     * @param userSitesDTO the userSitesDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new userSitesDTO, or with status {@code 400 (Bad Request)} if the userSites has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/user-sites")
    public ResponseEntity<UserSitesDTO> createUserSites(@RequestBody UserSitesDTO userSitesDTO) throws URISyntaxException {
        log.debug("REST request to save UserSites : {}", userSitesDTO);
        if (userSitesDTO.getId() != null) {
            throw new BadRequestAlertException("A new userSites cannot already have an ID", ENTITY_NAME, "idexists");
        }
        UserSitesDTO result = userSitesService.save(userSitesDTO);
        return ResponseEntity
            .created(new URI("/api/user-sites/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /user-sites/:id} : Updates an existing userSites.
     *
     * @param id the id of the userSitesDTO to save.
     * @param userSitesDTO the userSitesDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated userSitesDTO,
     * or with status {@code 400 (Bad Request)} if the userSitesDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the userSitesDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/user-sites/{id}")
    public ResponseEntity<UserSitesDTO> updateUserSites(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody UserSitesDTO userSitesDTO
    ) throws URISyntaxException {
        log.debug("REST request to update UserSites : {}, {}", id, userSitesDTO);
        if (userSitesDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, userSitesDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!userSitesRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        UserSitesDTO result = userSitesService.save(userSitesDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, userSitesDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /user-sites/:id} : Partial updates given fields of an existing userSites, field will ignore if it is null
     *
     * @param id the id of the userSitesDTO to save.
     * @param userSitesDTO the userSitesDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated userSitesDTO,
     * or with status {@code 400 (Bad Request)} if the userSitesDTO is not valid,
     * or with status {@code 404 (Not Found)} if the userSitesDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the userSitesDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/user-sites/{id}", consumes = "application/merge-patch+json")
    public ResponseEntity<UserSitesDTO> partialUpdateUserSites(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody UserSitesDTO userSitesDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update UserSites partially : {}, {}", id, userSitesDTO);
        if (userSitesDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, userSitesDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!userSitesRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<UserSitesDTO> result = userSitesService.partialUpdate(userSitesDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, userSitesDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /user-sites} : get all the userSites.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of userSites in body.
     */
    @GetMapping("/user-sites")
    public List<UserSitesDTO> getAllUserSites() {
        log.debug("REST request to get all UserSites");
        return userSitesService.findAll();
    }

    /**
     * {@code GET  /user-sites/:id} : get the "id" userSites.
     *
     * @param id the id of the userSitesDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the userSitesDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/user-sites/{id}")
    public ResponseEntity<UserSitesDTO> getUserSites(@PathVariable Long id) {
        log.debug("REST request to get UserSites : {}", id);
        Optional<UserSitesDTO> userSitesDTO = userSitesService.findOne(id);
        return ResponseUtil.wrapOrNotFound(userSitesDTO);
    }

    /**
     * {@code DELETE  /user-sites/:id} : delete the "id" userSites.
     *
     * @param id the id of the userSitesDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/user-sites/{id}")
    public ResponseEntity<Void> deleteUserSites(@PathVariable Long id) {
        log.debug("REST request to delete UserSites : {}", id);
        userSitesService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /_search/user-sites?query=:query} : search for the userSites corresponding
     * to the query.
     *
     * @param query the query of the userSites search.
     * @return the result of the search.
     */
    @GetMapping("/_search/user-sites")
    public List<UserSitesDTO> searchUserSites(@RequestParam String query) {
        log.debug("REST request to search UserSites for query {}", query);
        return userSitesService.search(query);
    }
}
