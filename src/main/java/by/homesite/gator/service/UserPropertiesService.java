package by.homesite.gator.service;

import by.homesite.gator.service.dto.UserPropertiesDTO;

import java.util.List;
import java.util.Optional;

/**
 * Service Interface for managing {@link by.homesite.gator.domain.UserProperties}.
 */
public interface UserPropertiesService {

    /**
     * Save a userProperties.
     *
     * @param userPropertiesDTO the entity to save.
     * @return the persisted entity.
     */
    UserPropertiesDTO save(UserPropertiesDTO userPropertiesDTO);

    /**
     * Get all the userProperties.
     *
     * @return the list of entities.
     */
    List<UserPropertiesDTO> findAll();


    /**
     * Get the "id" userProperties.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<UserPropertiesDTO> findOne(Long id);

    /**
     * Delete the "id" userProperties.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the userProperties corresponding to the query.
     *
     * @param query the query of the search.
     * 
     * @return the list of entities.
     */
    List<UserPropertiesDTO> search(String query);
}
