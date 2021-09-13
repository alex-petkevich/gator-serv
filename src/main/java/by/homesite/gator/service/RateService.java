package by.homesite.gator.service;

import by.homesite.gator.service.dto.RateDTO;
import java.util.List;
import java.util.Optional;

/**
 * Service Interface for managing {@link by.homesite.gator.domain.Rate}.
 */
public interface RateService {
    /**
     * Save a rate.
     *
     * @param rateDTO the entity to save.
     * @return the persisted entity.
     */
    RateDTO save(RateDTO rateDTO);

    /**
     * Partially updates a rate.
     *
     * @param rateDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<RateDTO> partialUpdate(RateDTO rateDTO);

    /**
     * Get all the rates.
     *
     * @return the list of entities.
     */
    List<RateDTO> findAll();

    /**
     * Get the "id" rate.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<RateDTO> findOne(Long id);

    Optional<RateDTO> findByCode(String code);

    /**
     * Delete the "id" rate.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    void fetchRates();
}
