package by.homesite.gator.service;

import by.homesite.gator.service.dto.PropertiesDTO;

import java.util.List;
import java.util.Optional;

/**
 * Service Interface for managing {@link by.homesite.gator.domain.Properties}.
 */
public interface PropertiesService {

    /**
     * Save a properties.
     *
     * @param propertiesDTO the entity to save.
     * @return the persisted entity.
     */
    PropertiesDTO save(PropertiesDTO propertiesDTO);

    /**
     * Get all the properties.
     *
     * @return the list of entities.
     */
    List<PropertiesDTO> findAll();


    /**
     * Get the "id" properties.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<PropertiesDTO> findOne(Long id);

    /**
     * Delete the "id" properties.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the properties corresponding to the query.
     *
     * @param query the query of the search.
     * 
     * @return the list of entities.
     */
    List<PropertiesDTO> search(String query);
}
