package by.homesite.gator.web.rest;

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

import by.homesite.gator.repository.UserRepository;
import by.homesite.gator.security.SecurityUtils;
import by.homesite.gator.service.UserSearchesService;
import by.homesite.gator.service.dto.UserSearchesDTO;
import by.homesite.gator.web.rest.errors.BadRequestAlertException;
import io.github.jhipster.web.util.HeaderUtil;
import io.github.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link by.homesite.gator.domain.UserSearches}.
 */
@RestController
@RequestMapping("/api")
public class UserSearchesResource
{

    private final Logger log = LoggerFactory.getLogger(UserSearchesResource.class);

    private static final String ENTITY_NAME = "userSearches";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final UserSearchesService userSearchesService;
    private UserRepository userRepository;

    public UserSearchesResource(UserSearchesService userSearchesService, UserRepository userRepository) {
        this.userSearchesService = userSearchesService;
        this.userRepository = userRepository;
    }

    /**
     * {@code POST  /user-searches} : Create a new userSearches.
     *
     * @param userSearchesDTO the userSearchesDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new userSearchesDTO, or with status {@code 400 (Bad Request)} if the userSearches has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/user-searches")
    public ResponseEntity<UserSearchesDTO> createUserSearches(@RequestBody UserSearchesDTO userSearchesDTO) throws URISyntaxException {
        log.debug("REST request to save UserSearches : {}", userSearchesDTO);
        if (userSearchesDTO.getId() != null) {
            throw new BadRequestAlertException("A new userSearches cannot already have an ID", ENTITY_NAME, "idexists");
        }
        if (userSearchesService.findByName(userSearchesDTO.getName()).size() > 0) {
            throw new BadRequestAlertException("Search with this name already exists", ENTITY_NAME, "nameexists");
        }
        if (!SecurityUtils.isAuthenticated()) {
            throw new BadRequestAlertException("User not logged in", ENTITY_NAME, "loginrequired");
        }

        Long userId = userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin().get()).get().getId() ;
        userSearchesDTO.setUserId(userId);

        UserSearchesDTO result = userSearchesService.save(userSearchesDTO);
        return ResponseEntity.created(new URI("/api/user-searches/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /user-searches} : Updates an existing userSearches.
     *
     * @param userSearchesDTO the userSearchesDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated userSearchesDTO,
     * or with status {@code 400 (Bad Request)} if the userSearchesDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the userSearchesDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/user-searches")
    public ResponseEntity<UserSearchesDTO> updateUserSearches(@RequestBody UserSearchesDTO userSearchesDTO) throws URISyntaxException {
        log.debug("REST request to update UserSearches : {}", userSearchesDTO);
        if (userSearchesDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        UserSearchesDTO result = userSearchesService.save(userSearchesDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, userSearchesDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /user-searches} : get all the userSearches.
     *

     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of userSearches in body.
     */
    @GetMapping("/user-searches")
    public List<UserSearchesDTO> getUserSearches() {
        log.debug("REST request to get all UserSearches");
        return userSearchesService.findUserSearches();
    }

    /**
     * {@code GET  /user-searches/:id} : get the "id" userSearches.
     *
     * @param id the id of the userSearchesDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the userSearchesDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/user-searches/{id}")
    public ResponseEntity<UserSearchesDTO> getUserSearches(@PathVariable Long id) {
        log.debug("REST request to get UserSearches : {}", id);
        Optional<UserSearchesDTO> userSearchesDTO = userSearchesService.findOne(id);
        return ResponseUtil.wrapOrNotFound(userSearchesDTO);
    }

    /**
     * {@code DELETE  /user-searches/:id} : delete the "id" userSearches.
     *
     * @param id the id of the userSearchesDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/user-searches/{id}")
    public ResponseEntity<Void> deleteUserSearches(@PathVariable Long id) {
        log.debug("REST request to delete UserSearches : {}", id);
        userSearchesService.delete(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build();
    }

}
