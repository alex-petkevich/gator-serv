package by.homesite.gator.service.impl;

import static org.elasticsearch.index.query.QueryBuilders.*;

import by.homesite.gator.domain.UserSites;
import by.homesite.gator.repository.UserSitesRepository;
import by.homesite.gator.repository.search.UserSitesSearchRepository;
import by.homesite.gator.service.UserSitesService;
import by.homesite.gator.service.dto.UserSitesDTO;
import by.homesite.gator.service.mapper.UserSitesMapper;
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
 * Service Implementation for managing {@link UserSites}.
 */
@Service
@Transactional
public class UserSitesServiceImpl implements UserSitesService {

    private final Logger log = LoggerFactory.getLogger(UserSitesServiceImpl.class);

    private final UserSitesRepository userSitesRepository;

    private final UserSitesMapper userSitesMapper;

    private final UserSitesSearchRepository userSitesSearchRepository;

    public UserSitesServiceImpl(
        UserSitesRepository userSitesRepository,
        UserSitesMapper userSitesMapper,
        UserSitesSearchRepository userSitesSearchRepository
    ) {
        this.userSitesRepository = userSitesRepository;
        this.userSitesMapper = userSitesMapper;
        this.userSitesSearchRepository = userSitesSearchRepository;
    }

    @Override
    public UserSitesDTO save(UserSitesDTO userSitesDTO) {
        log.debug("Request to save UserSites : {}", userSitesDTO);
        UserSites userSites = userSitesMapper.toEntity(userSitesDTO);
        userSites = userSitesRepository.save(userSites);
        UserSitesDTO result = userSitesMapper.toDto(userSites);
        userSitesSearchRepository.save(userSites);
        return result;
    }

    @Override
    public Optional<UserSitesDTO> partialUpdate(UserSitesDTO userSitesDTO) {
        log.debug("Request to partially update UserSites : {}", userSitesDTO);

        return userSitesRepository
            .findById(userSitesDTO.getId())
            .map(
                existingUserSites -> {
                    userSitesMapper.partialUpdate(existingUserSites, userSitesDTO);

                    return existingUserSites;
                }
            )
            .map(userSitesRepository::save)
            .map(
                savedUserSites -> {
                    userSitesSearchRepository.save(savedUserSites);

                    return savedUserSites;
                }
            )
            .map(userSitesMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserSitesDTO> findAll() {
        log.debug("Request to get all UserSites");
        return userSitesRepository.findAll().stream().map(userSitesMapper::toDto).collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserSitesDTO> findOne(Long id) {
        log.debug("Request to get UserSites : {}", id);
        return userSitesRepository.findById(id).map(userSitesMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete UserSites : {}", id);
        userSitesRepository.deleteById(id);
        userSitesSearchRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserSitesDTO> search(String query) {
        log.debug("Request to search UserSites for query {}", query);
        return StreamSupport
            .stream(userSitesSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .map(userSitesMapper::toDto)
            .collect(Collectors.toList());
    }
}
