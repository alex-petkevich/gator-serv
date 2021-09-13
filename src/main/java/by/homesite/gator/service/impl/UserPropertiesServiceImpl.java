package by.homesite.gator.service.impl;

import static org.elasticsearch.index.query.QueryBuilders.*;

import by.homesite.gator.domain.UserProperties;
import by.homesite.gator.repository.UserPropertiesRepository;
import by.homesite.gator.repository.search.UserPropertiesSearchRepository;
import by.homesite.gator.service.UserPropertiesService;
import by.homesite.gator.service.dto.UserPropertiesDTO;
import by.homesite.gator.service.mapper.UserPropertiesMapper;
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
 * Service Implementation for managing {@link UserProperties}.
 */
@Service
@Transactional
public class UserPropertiesServiceImpl implements UserPropertiesService {

    private final Logger log = LoggerFactory.getLogger(UserPropertiesServiceImpl.class);

    private final UserPropertiesRepository userPropertiesRepository;

    private final UserPropertiesMapper userPropertiesMapper;

    private final UserPropertiesSearchRepository userPropertiesSearchRepository;

    public UserPropertiesServiceImpl(
        UserPropertiesRepository userPropertiesRepository,
        UserPropertiesMapper userPropertiesMapper,
        UserPropertiesSearchRepository userPropertiesSearchRepository
    ) {
        this.userPropertiesRepository = userPropertiesRepository;
        this.userPropertiesMapper = userPropertiesMapper;
        this.userPropertiesSearchRepository = userPropertiesSearchRepository;
    }

    @Override
    public UserPropertiesDTO save(UserPropertiesDTO userPropertiesDTO) {
        log.debug("Request to save UserProperties : {}", userPropertiesDTO);
        UserProperties userProperties = userPropertiesMapper.toEntity(userPropertiesDTO);
        userProperties = userPropertiesRepository.save(userProperties);
        UserPropertiesDTO result = userPropertiesMapper.toDto(userProperties);
        userPropertiesSearchRepository.save(userProperties);
        return result;
    }

    @Override
    public Optional<UserPropertiesDTO> partialUpdate(UserPropertiesDTO userPropertiesDTO) {
        log.debug("Request to partially update UserProperties : {}", userPropertiesDTO);

        return userPropertiesRepository
            .findById(userPropertiesDTO.getId())
            .map(
                existingUserProperties -> {
                    userPropertiesMapper.partialUpdate(existingUserProperties, userPropertiesDTO);

                    return existingUserProperties;
                }
            )
            .map(userPropertiesRepository::save)
            .map(
                savedUserProperties -> {
                    userPropertiesSearchRepository.save(savedUserProperties);

                    return savedUserProperties;
                }
            )
            .map(userPropertiesMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserPropertiesDTO> findAll() {
        log.debug("Request to get all UserProperties");
        return userPropertiesRepository
            .findAll()
            .stream()
            .map(userPropertiesMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserPropertiesDTO> findOne(Long id) {
        log.debug("Request to get UserProperties : {}", id);
        return userPropertiesRepository.findById(id).map(userPropertiesMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete UserProperties : {}", id);
        userPropertiesRepository.deleteById(id);
        userPropertiesSearchRepository.deleteById(id);
    }

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
