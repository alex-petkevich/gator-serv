package by.homesite.gator.service;

import by.homesite.gator.service.dto.ItemDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link by.homesite.gator.domain.Item}.
 */
public interface ItemService {
    /**
     * Save a item.
     *
     * @param itemDTO the entity to save.
     * @return the persisted entity.
     */
    ItemDTO save(ItemDTO itemDTO);

    /**
     * Partially updates a item.
     *
     * @param itemDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<ItemDTO> partialUpdate(ItemDTO itemDTO);

    /**
     * Get all the items.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<ItemDTO> findAll(Pageable pageable);

    /**
     * Get the "id" item.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<ItemDTO> findOne(Long id);

    /**
     * Delete the "id" item.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the item corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param category
     * @param type
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<ItemDTO> search(String query, String category, String type, Pageable pageable);

    void deleteOldItems(int days);
}
