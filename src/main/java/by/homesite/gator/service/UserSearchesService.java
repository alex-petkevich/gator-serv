package by.homesite.gator.service;

import java.util.List;
import java.util.Optional;

import by.homesite.gator.service.dto.UserSearchesDTO;

/**
 * Service Interface for managing {@link by.homesite.gator.domain.UserSearches}.
 */
public interface UserSearchesService
{

    /**
     * Save a userProperties.
     *
     * @param userSearchesDTO the entity to save.
     * @return the persisted entity.
     */
    UserSearchesDTO save(UserSearchesDTO userSearchesDTO);

    /**
     * Get all the userProperties.
     *
     * @return the list of entities.
     */
    List<UserSearchesDTO> findAll();

    /**
     * Get the "id" userProperties.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<UserSearchesDTO> findOne(Long id);

    List<UserSearchesDTO> findUserSearches();

    /**
     * Delete the "id" userProperties.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
