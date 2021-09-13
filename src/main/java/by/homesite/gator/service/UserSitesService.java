package by.homesite.gator.service;

import by.homesite.gator.service.dto.UserSitesDTO;

import java.util.List;
import java.util.Optional;

/**
 * Service Interface for managing {@link by.homesite.gator.domain.UserSites}.
 */
public interface UserSitesService {

    /**
     * Save a userSites.
     *
     * @param userSitesDTO the entity to save.
     * @return the persisted entity.
     */
    UserSitesDTO save(UserSitesDTO userSitesDTO);

    /**
     * Get all the userSites.
     *
     * @return the list of entities.
     */
    List<UserSitesDTO> findAll();


    /**
     * Get the "id" userSites.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<UserSitesDTO> findOne(Long id);

    /**
     * Delete the "id" userSites.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the userSites corresponding to the query.
     *
     * @param query the query of the search.
     * 
     * @return the list of entities.
     */
    List<UserSitesDTO> search(String query);
}
