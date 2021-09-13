package by.homesite.gator.service;

import by.homesite.gator.service.dto.SiteDTO;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * Service Interface for managing {@link by.homesite.gator.domain.Site}.
 */
public interface SiteService {

    /**
     * Save a site.
     *
     * @param siteDTO the entity to save.
     * @return the persisted entity.
     */
    SiteDTO save(SiteDTO siteDTO);

    /**
     * Get all the sites.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<SiteDTO> findAll(Pageable pageable);


    /**
     * Get the "id" site.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<SiteDTO> findOne(Long id);

    /**
     * Delete the "id" site.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the site corresponding to the query.
     *
     * @param query the query of the search.
     * 
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<SiteDTO> search(String query, Pageable pageable);
}
