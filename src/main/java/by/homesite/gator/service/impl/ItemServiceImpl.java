package by.homesite.gator.service.impl;

import by.homesite.gator.domain.Item;
import by.homesite.gator.repository.ItemRepository;
import by.homesite.gator.repository.search.ItemSearchRepository;
import by.homesite.gator.service.CategoryService;
import by.homesite.gator.service.ItemService;
import by.homesite.gator.service.dto.ItemDTO;
import by.homesite.gator.service.mapper.CategoryMapper;
import by.homesite.gator.service.mapper.ItemMapper;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.index.query.QueryBuilders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public ItemServiceImpl(
        ItemRepository itemRepository,
        ItemMapper itemMapper,
        ItemSearchRepository itemSearchRepository,
        CategoryService categoryService,
        CategoryMapper categoryMapper
    ) {
        this.itemRepository = itemRepository;
        this.itemMapper = itemMapper;
        this.itemSearchRepository = itemSearchRepository;
        this.categoryService = categoryService;
        this.categoryMapper = categoryMapper;
    }

    @Override
    public ItemDTO save(ItemDTO itemDTO) {
        log.debug("Request to save Item : {}", itemDTO);
        Item item = itemMapper.toEntity(itemDTO);
        if (itemDTO.getCategoryId() != null) {
            item.setCategory(categoryMapper.toEntity(categoryService.findOne(itemDTO.getCategoryId()).get()));
        }
        if (item.getCreatedAt() == null) item.setCreatedAt(ZonedDateTime.now());
        if (item.getUpdatedAt() == null) item.setUpdatedAt(ZonedDateTime.now());
        item = itemRepository.save(item);
        ItemDTO result = itemMapper.toDto(item);
        itemSearchRepository.save(item);
        return result;
    }

    @Override
    public Optional<ItemDTO> partialUpdate(ItemDTO itemDTO) {
        log.debug("Request to partially update Item : {}", itemDTO);

        return itemRepository
            .findById(itemDTO.getId())
            .map(
                existingItem -> {
                    itemMapper.partialUpdate(existingItem, itemDTO);

                    return existingItem;
                }
            )
            .map(itemRepository::save)
            .map(
                savedItem -> {
                    itemSearchRepository.save(savedItem);

                    return savedItem;
                }
            )
            .map(itemMapper::toDto);
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
        return itemRepository.findAll(pageable).map(itemMapper::toDto);
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
        return itemRepository.findById(id).map(itemMapper::toDto);
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
     * @param category
     * @param type
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Override
    @Transactional(readOnly = true)
    public Page<ItemDTO> search(String query, String category, String type, Pageable pageable) {
        log.trace("Request to search for a page of Items for query {}", query);
        StringBuilder inputQuery = new StringBuilder("active:true");

        if (!StringUtils.isEmpty(query) && query.contains("nativeId:") && !"*".equals(type)) {
            inputQuery.append(" AND (").append(query).append(")");
        } else if (!StringUtils.isEmpty(query) && !"*".equals(type)) {
            inputQuery.append(" AND (title:").append(query).append(" OR description:").append(query).append(")");
        }

        if (!StringUtils.isEmpty(category) && !"0".equals(category)) {
            inputQuery.append(" AND (category.id:").append(category.replaceAll(",", " OR category.id:")).append(")");
        }

        if (!StringUtils.isEmpty(type) && !"undefined".equals(type)) {
            inputQuery.append(" AND (type:").append(type.replaceAll(",", " OR type:")).append(")");
        }

        /* NativeSearchQueryBuilder searchQueryBuilder = new NativeSearchQueryBuilder();
        BoolQueryBuilder boolQuery = new BoolQueryBuilder();

        boolQuery.must(matchQuery("active","true").operator(Operator.AND));



        if (!StringUtils.isEmpty(query))
        {
            boolQuery.must(multiMatchQuery(query)
                .field("title")
                .field("description")
                .type(MultiMatchQueryBuilder.Type.BEST_FIELDS)
                .fuzziness(Fuzziness.ONE)
                .prefixLength(3)
                .operator(Operator.AND)
            );
        }
        if (!StringUtils.isEmpty(category) && !"0".equals(category))
        {
            boolQuery.must(
                queryStringQuery("(category.id:" + category.replaceAll(",", " OR category.id:") + ")")
            );
        }
        if (!StringUtils.isEmpty(type) && !"undefined".equals(type)) {
            boolQuery.must(
                queryStringQuery("(type:" + type.replaceAll(",", " OR type:") + ")")
            );
        }
        searchQueryBuilder.withQuery(boolQuery);

        NativeSearchQuery searchQuery = searchQueryBuilder.build();*/

        return itemSearchRepository
            .search(QueryBuilders.queryStringQuery(inputQuery.toString()).fuzziness(Fuzziness.ONE).fuzzyPrefixLength(3), pageable)
            .map(itemMapper::toDto);
    }

    @Override
    public void deleteOldItems(int days) {
        log.debug("Request to delete old items");
        List<Item> deletedItems = itemRepository.findOldItems(ZonedDateTime.now().minusDays(days));
        itemRepository.deleteOldItems(ZonedDateTime.now().minusDays(days));
        deletedItems.forEach(el -> itemSearchRepository.deleteById(el.getId()));
    }
}
