package by.homesite.gator.service.impl;

import static org.elasticsearch.index.query.QueryBuilders.*;

import by.homesite.gator.domain.Properties;
import by.homesite.gator.repository.PropertiesRepository;
import by.homesite.gator.repository.search.PropertiesSearchRepository;
import by.homesite.gator.service.PropertiesService;
import by.homesite.gator.service.dto.PropertiesDTO;
import by.homesite.gator.service.mapper.PropertiesMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public PropertiesServiceImpl(
        PropertiesRepository propertiesRepository,
        PropertiesMapper propertiesMapper,
        PropertiesSearchRepository propertiesSearchRepository
    ) {
        this.propertiesRepository = propertiesRepository;
        this.propertiesMapper = propertiesMapper;
        this.propertiesSearchRepository = propertiesSearchRepository;
    }

    @Override
    public PropertiesDTO save(PropertiesDTO propertiesDTO) {
        log.debug("Request to save Properties : {}", propertiesDTO);
        Properties properties = propertiesMapper.toEntity(propertiesDTO);
        properties = propertiesRepository.save(properties);
        PropertiesDTO result = propertiesMapper.toDto(properties);
        propertiesSearchRepository.save(properties);
        return result;
    }

    @Override
    public Optional<PropertiesDTO> partialUpdate(PropertiesDTO propertiesDTO) {
        log.debug("Request to partially update Properties : {}", propertiesDTO);

        return propertiesRepository
            .findById(propertiesDTO.getId())
            .map(
                existingProperties -> {
                    propertiesMapper.partialUpdate(existingProperties, propertiesDTO);

                    return existingProperties;
                }
            )
            .map(propertiesRepository::save)
            .map(
                savedProperties -> {
                    propertiesSearchRepository.save(savedProperties);

                    return savedProperties;
                }
            )
            .map(propertiesMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PropertiesDTO> findAll() {
        log.debug("Request to get all Properties");
        return propertiesRepository.findAll().stream().map(propertiesMapper::toDto).collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PropertiesDTO> findOne(Long id) {
        log.debug("Request to get Properties : {}", id);
        return propertiesRepository.findById(id).map(propertiesMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Properties : {}", id);
        propertiesRepository.deleteById(id);
        propertiesSearchRepository.deleteById(id);
    }

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
