package by.homesite.gator.service.impl;

import by.homesite.gator.service.CategoryService;
import by.homesite.gator.service.ItemService;
import by.homesite.gator.domain.Item;
import by.homesite.gator.repository.ItemRepository;
import by.homesite.gator.repository.search.ItemSearchRepository;
import by.homesite.gator.service.dto.ItemDTO;
import by.homesite.gator.service.mapper.CategoryMapper;
import by.homesite.gator.service.mapper.ItemMapper;
import io.swagger.models.auth.In;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing {@link Item}.
 */
@Service
@Transactional
public class ItemServiceImpl implements ItemService {

    private final Logger log = LoggerFactory.getLogger(ItemServiceImpl.class);

    private final ItemRepository itemRepository;

    private final ItemMapper itemMapper;

    private final ItemSearchRepository itemSearchRepository;

    private final CategoryService categoryService;

    private final CategoryMapper categoryMapper;

    public ItemServiceImpl(ItemRepository itemRepository, ItemMapper itemMapper, ItemSearchRepository itemSearchRepository, CategoryService categoryService, CategoryMapper categoryMapper) {
        this.itemRepository = itemRepository;
        this.itemMapper = itemMapper;
        this.itemSearchRepository = itemSearchRepository;
        this.categoryService = categoryService;
        this.categoryMapper = categoryMapper;
    }

    /**
     * Save a item.
     *
     * @param itemDTO the entity to save.
     * @return the persisted entity.
     */
    @Override
    public ItemDTO save(ItemDTO itemDTO) {
        log.debug("Request to save Item : {}", itemDTO);
        Item item = itemMapper.toEntity(itemDTO);
        if (itemDTO.getCategoryId() != null) {
            item.setCategory(categoryMapper.toEntity(categoryService.findOne(itemDTO.getCategoryId()).get()));
        }
        if (item.getCreatedAt() == null)
            item.setCreatedAt(ZonedDateTime.now());
        if (item.getUpdatedAt() == null)
            item.setUpdatedAt(ZonedDateTime.now());
        item = itemRepository.save(item);
        ItemDTO result = itemMapper.toDto(item);
        itemSearchRepository.save(item);
        return result;
    }

    /**
     * Get all the items.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Override
    @Transactional(readOnly = true)
    public Page<ItemDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Items");
        return itemRepository.findAll(pageable)
            .map(itemMapper::toDto);
    }


    /**
     * Get one item by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<ItemDTO> findOne(Long id) {
        log.debug("Request to get Item : {}", id);
        return itemRepository.findById(id)
            .map(itemMapper::toDto);
    }

    /**
     * Delete the item by id.
     *
     * @param id the id of the entity.
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete Item : {}", id);
        itemRepository.deleteById(id);
        itemSearchRepository.deleteById(id);
    }

    /**
     * Search for the item corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Override
    @Transactional(readOnly = true)
    public Page<ItemDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Items for query {}", query);
        return itemSearchRepository.search(queryStringQuery(query), pageable)
            .map(itemMapper::toDto);
    }

    @Override
    public void deleteOldItems(int days)
    {
        log.debug("Request to delete old items");
        List<Integer> deletedItems = itemRepository.deleteOldItems(LocalDate.now().minusDays(days));
        deletedItems.forEach(el -> itemSearchRepository.deleteById(el.longValue()));
    }
}
