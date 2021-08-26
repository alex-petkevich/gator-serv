package by.homesite.gator.service.impl;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import by.homesite.gator.domain.UserSearches;
import by.homesite.gator.repository.UserSearchesRepository;
import by.homesite.gator.service.ItemService;
import by.homesite.gator.service.UserSearchesService;
import by.homesite.gator.service.dto.ItemDTO;
import by.homesite.gator.service.dto.UserSearchesDTO;
import by.homesite.gator.service.mapper.UserSearchesMapper;

/**
 * Service Implementation for managing {@link UserSearches}.
 */
@Service
@Transactional
public class UserSearchesServiceImpl implements UserSearchesService
{

    private final Logger log = LoggerFactory.getLogger(UserSearchesServiceImpl.class);

    private final UserSearchesRepository userSearchesRepository;

    private final UserSearchesMapper userSearchesMapper;
    private final ItemService itemService;

    public UserSearchesServiceImpl(UserSearchesRepository userSearchesRepository, UserSearchesMapper userSearchesMapper, ItemService itemService) {
        this.userSearchesRepository = userSearchesRepository;
        this.userSearchesMapper = userSearchesMapper;
        this.itemService = itemService;
    }

    /**
     * Save a userSearches.
     *
     * @param userSearchesDTO the entity to save.
     * @return the persisted entity.
     */
    @Override
    public UserSearchesDTO save(UserSearchesDTO userSearchesDTO) {
        log.debug("Request to save UserSearches : {}", userSearchesDTO);
        UserSearches userSearches = userSearchesMapper.toEntity(userSearchesDTO);
        userSearchesRepository.save(userSearches);
        userSearchesDTO = userSearchesMapper.toDto(userSearches);
        return userSearchesDTO;
    }

    /**
     * Get all the userProperties.
     *
     * @return the list of entities.
     */
    @Override
    @Transactional(readOnly = true)
    public List<UserSearchesDTO> findAll() {
        log.debug("Request to get all UserSearches");
        return userSearchesRepository.findAll().stream()
            .map(userSearchesMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }


    /**
     * Get one userProperties by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<UserSearchesDTO> findOne(Long id) {
        log.debug("Request to get UserSearches : {}", id);
        return userSearchesRepository.findById(id)
            .map(userSearchesMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserSearchesDTO> findByName(String name)
    {
        log.debug("Request to get UserSearches by name : {}", name);
        return userSearchesRepository.findByName(name).stream()
            .map(userSearchesMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserSearchesDTO> findUserSearches()
    {
        log.debug("Request to get user searches");
        return userSearchesRepository.findByUserIsCurrentUser().stream()
            .map(userSearchesMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Delete the userProperties by id.
     *
     * @param id the id of the entity.
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete UserSearches : {}", id);
        userSearchesRepository.deleteById(id);
    }

    @Override
    public boolean checkIfItemEligible(UserSearches userSearches, ItemDTO item)
    {
        // запросить эластик и проверить есть ли элемент в результатах
        ObjectMapper objectMapper = new ObjectMapper();
        try
        {
            JsonNode jsonNode = objectMapper.readTree(userSearches.getPayload());

            Page<ItemDTO> itemDTOS = itemService.search(
                getJsonTextValue(jsonNode, "currentSearch"), getJsonTextValue(jsonNode, "searchCategory"),
                getJsonTextValue(jsonNode, "searchType"), Pageable.unpaged());

            return itemDTOS.stream().anyMatch(currentItem -> currentItem.getId().equals(item.getId()));
        }
        catch (IOException e)
        {
            log.error("JSON can not be parsed: {}", e.getMessage());
        }


        return false;
    }

    private String getJsonTextValue(JsonNode jsonNode, String currentSearch)
    {
        return jsonNode.get(currentSearch) == null ? "" : jsonNode.get(currentSearch).asText();
    }

}
