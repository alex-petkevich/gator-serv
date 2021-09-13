package by.homesite.gator.service.impl;

import by.homesite.gator.service.UserPropertiesService;
import by.homesite.gator.domain.UserProperties;
import by.homesite.gator.repository.UserPropertiesRepository;
import by.homesite.gator.repository.search.UserPropertiesSearchRepository;
import by.homesite.gator.service.dto.UserPropertiesDTO;
import by.homesite.gator.service.mapper.UserPropertiesMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing {@link UserProperties}.
 */
@Service
@Transactional
public class UserPropertiesServiceImpl implements UserPropertiesService {

    private final Logger log = LoggerFactory.getLogger(UserPropertiesServiceImpl.class);

    private final UserPropertiesRepository userPropertiesRepository;

    private final UserPropertiesMapper userPropertiesMapper;

    private final UserPropertiesSearchRepository userPropertiesSearchRepository;

    public UserPropertiesServiceImpl(UserPropertiesRepository userPropertiesRepository, UserPropertiesMapper userPropertiesMapper, UserPropertiesSearchRepository userPropertiesSearchRepository) {
        this.userPropertiesRepository = userPropertiesRepository;
        this.userPropertiesMapper = userPropertiesMapper;
        this.userPropertiesSearchRepository = userPropertiesSearchRepository;
    }

    /**
     * Save a userProperties.
     *
     * @param userPropertiesDTO the entity to save.
     * @return the persisted entity.
     */
    @Override
    public UserPropertiesDTO save(UserPropertiesDTO userPropertiesDTO) {
        log.debug("Request to save UserProperties : {}", userPropertiesDTO);
        UserProperties userProperties = userPropertiesMapper.toEntity(userPropertiesDTO);
        userProperties = userPropertiesRepository.save(userProperties);
        UserPropertiesDTO result = userPropertiesMapper.toDto(userProperties);
        userPropertiesSearchRepository.save(userProperties);
        return result;
    }

    /**
     * Get all the userProperties.
     *
     * @return the list of entities.
     */
    @Override
    @Transactional(readOnly = true)
    public List<UserPropertiesDTO> findAll() {
        log.debug("Request to get all UserProperties");
        return userPropertiesRepository.findAll().stream()
            .map(userPropertiesMapper::toDto)
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
    public Optional<UserPropertiesDTO> findOne(Long id) {
        log.debug("Request to get UserProperties : {}", id);
        return userPropertiesRepository.findById(id)
            .map(userPropertiesMapper::toDto);
    }

    /**
     * Delete the userProperties by id.
     *
     * @param id the id of the entity.
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete UserProperties : {}", id);
        userPropertiesRepository.deleteById(id);
        userPropertiesSearchRepository.deleteById(id);
    }

    /**
     * Search for the userProperties corresponding to the query.
     *
     * @param query the query of the search.
     * @return the list of entities.
     */
    @Override
    @Transactional(readOnly = true)
    public List<UserPropertiesDTO> search(String query) {
        log.debug("Request to search UserProperties for query {}", query);
        return StreamSupport
            .stream(userPropertiesSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .map(userPropertiesMapper::toDto)
            .collect(Collectors.toList());
    }
}
