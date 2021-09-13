package by.homesite.gator.service.impl;

import by.homesite.gator.service.PropertiesService;
import by.homesite.gator.domain.Properties;
import by.homesite.gator.repository.PropertiesRepository;
import by.homesite.gator.repository.search.PropertiesSearchRepository;
import by.homesite.gator.service.dto.PropertiesDTO;
import by.homesite.gator.service.mapper.PropertiesMapper;
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
 * Service Implementation for managing {@link Properties}.
 */
@Service
@Transactional
public class PropertiesServiceImpl implements PropertiesService {

    private final Logger log = LoggerFactory.getLogger(PropertiesServiceImpl.class);

    private final PropertiesRepository propertiesRepository;

    private final PropertiesMapper propertiesMapper;

    private final PropertiesSearchRepository propertiesSearchRepository;

    public PropertiesServiceImpl(PropertiesRepository propertiesRepository, PropertiesMapper propertiesMapper, PropertiesSearchRepository propertiesSearchRepository) {
        this.propertiesRepository = propertiesRepository;
        this.propertiesMapper = propertiesMapper;
        this.propertiesSearchRepository = propertiesSearchRepository;
    }

    /**
     * Save a properties.
     *
     * @param propertiesDTO the entity to save.
     * @return the persisted entity.
     */
    @Override
    public PropertiesDTO save(PropertiesDTO propertiesDTO) {
        log.debug("Request to save Properties : {}", propertiesDTO);
        Properties properties = propertiesMapper.toEntity(propertiesDTO);
        properties = propertiesRepository.save(properties);
        PropertiesDTO result = propertiesMapper.toDto(properties);
        propertiesSearchRepository.save(properties);
        return result;
    }

    /**
     * Get all the properties.
     *
     * @return the list of entities.
     */
    @Override
    @Transactional(readOnly = true)
    public List<PropertiesDTO> findAll() {
        log.debug("Request to get all Properties");
        return propertiesRepository.findAll().stream()
            .map(propertiesMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }


    /**
     * Get one properties by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<PropertiesDTO> findOne(Long id) {
        log.debug("Request to get Properties : {}", id);
        return propertiesRepository.findById(id)
            .map(propertiesMapper::toDto);
    }

    /**
     * Delete the properties by id.
     *
     * @param id the id of the entity.
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete Properties : {}", id);
        propertiesRepository.deleteById(id);
        propertiesSearchRepository.deleteById(id);
    }

    /**
     * Search for the properties corresponding to the query.
     *
     * @param query the query of the search.
     * @return the list of entities.
     */
    @Override
    @Transactional(readOnly = true)
    public List<PropertiesDTO> search(String query) {
        log.debug("Request to search Properties for query {}", query);
        return StreamSupport
            .stream(propertiesSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .map(propertiesMapper::toDto)
            .collect(Collectors.toList());
    }
}
