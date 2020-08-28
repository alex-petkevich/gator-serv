package by.homesite.gator.service.impl;

import by.homesite.gator.service.SiteService;
import by.homesite.gator.domain.Site;
import by.homesite.gator.repository.SiteRepository;
import by.homesite.gator.repository.search.SiteSearchRepository;
import by.homesite.gator.service.dto.SiteDTO;
import by.homesite.gator.service.mapper.SiteMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing {@link Site}.
 */
@Service
@Transactional
public class SiteServiceImpl implements SiteService {

    private final Logger log = LoggerFactory.getLogger(SiteServiceImpl.class);

    private final SiteRepository siteRepository;

    private final SiteMapper siteMapper;

    private final SiteSearchRepository siteSearchRepository;

    public SiteServiceImpl(SiteRepository siteRepository, SiteMapper siteMapper, SiteSearchRepository siteSearchRepository) {
        this.siteRepository = siteRepository;
        this.siteMapper = siteMapper;
        this.siteSearchRepository = siteSearchRepository;
    }

    /**
     * Save a site.
     *
     * @param siteDTO the entity to save.
     * @return the persisted entity.
     */
    @Override
    public SiteDTO save(SiteDTO siteDTO) {
        log.debug("Request to save Site : {}", siteDTO);
        Site site = siteMapper.toEntity(siteDTO);
        site = siteRepository.save(site);
        SiteDTO result = siteMapper.toDto(site);
        siteSearchRepository.save(site);
        return result;
    }

    /**
     * Get all the sites.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Override
    @Transactional(readOnly = true)
    public Page<SiteDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Sites");
        return siteRepository.findAll(pageable)
            .map(siteMapper::toDto);
    }


    /**
     * Get one site by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<SiteDTO> findOne(Long id) {
        log.debug("Request to get Site : {}", id);
        return siteRepository.findById(id)
            .map(siteMapper::toDto);
    }

    /**
     * Delete the site by id.
     *
     * @param id the id of the entity.
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete Site : {}", id);
        siteRepository.deleteById(id);
        siteSearchRepository.deleteById(id);
    }

    /**
     * Search for the site corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Override
    @Transactional(readOnly = true)
    public Page<SiteDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Sites for query {}", query);
        return siteSearchRepository.search(queryStringQuery(query), pageable)
            .map(siteMapper::toDto);
    }
}
