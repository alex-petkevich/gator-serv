package by.homesite.gator.web.rest;

import static org.elasticsearch.index.query.QueryBuilders.*;

import by.homesite.gator.repository.RateRepository;
import by.homesite.gator.service.RateService;
import by.homesite.gator.service.dto.RateDTO;
import by.homesite.gator.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.StreamSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link by.homesite.gator.domain.Rate}.
 */
@RestController
@RequestMapping("/api")
public class RateResource {

    private final Logger log = LoggerFactory.getLogger(RateResource.class);

    private static final String ENTITY_NAME = "rate";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final RateService rateService;

    private final RateRepository rateRepository;

    public RateResource(RateService rateService, RateRepository rateRepository) {
        this.rateService = rateService;
        this.rateRepository = rateRepository;
    }

    /**
     * {@code POST  /rates} : Create a new rate.
     *
     * @param rateDTO the rateDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new rateDTO, or with status {@code 400 (Bad Request)} if the rate has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/rates")
    public ResponseEntity<RateDTO> createRate(@RequestBody RateDTO rateDTO) throws URISyntaxException {
        log.debug("REST request to save Rate : {}", rateDTO);
        if (rateDTO.getId() != null) {
            throw new BadRequestAlertException("A new rate cannot already have an ID", ENTITY_NAME, "idexists");
        }
        RateDTO result = rateService.save(rateDTO);
        return ResponseEntity
            .created(new URI("/api/rates/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /rates/:id} : Updates an existing rate.
     *
     * @param id the id of the rateDTO to save.
     * @param rateDTO the rateDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated rateDTO,
     * or with status {@code 400 (Bad Request)} if the rateDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the rateDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/rates/{id}")
    public ResponseEntity<RateDTO> updateRate(@PathVariable(value = "id", required = false) final Long id, @RequestBody RateDTO rateDTO)
        throws URISyntaxException {
        log.debug("REST request to update Rate : {}, {}", id, rateDTO);
        if (rateDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, rateDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!rateRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        RateDTO result = rateService.save(rateDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, rateDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /rates/:id} : Partial updates given fields of an existing rate, field will ignore if it is null
     *
     * @param id the id of the rateDTO to save.
     * @param rateDTO the rateDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated rateDTO,
     * or with status {@code 400 (Bad Request)} if the rateDTO is not valid,
     * or with status {@code 404 (Not Found)} if the rateDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the rateDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/rates/{id}", consumes = "application/merge-patch+json")
    public ResponseEntity<RateDTO> partialUpdateRate(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody RateDTO rateDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update Rate partially : {}, {}", id, rateDTO);
        if (rateDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, rateDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!rateRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<RateDTO> result = rateService.partialUpdate(rateDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, rateDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /rates} : get all the rates.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of rates in body.
     */
    @GetMapping("/rates")
    public List<RateDTO> getAllRates() {
        log.debug("REST request to get all Rates");
        return rateService.findAll();
    }

    /**
     * {@code GET  /rates/:id} : get the "id" rate.
     *
     * @param id the id of the rateDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the rateDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/rates/{id}")
    public ResponseEntity<RateDTO> getRate(@PathVariable Long id) {
        log.debug("REST request to get Rate : {}", id);
        Optional<RateDTO> rateDTO = rateService.findOne(id);
        return ResponseUtil.wrapOrNotFound(rateDTO);
    }

    /**
     * {@code DELETE  /rates/:id} : delete the "id" rate.
     *
     * @param id the id of the rateDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/rates/{id}")
    public ResponseEntity<Void> deleteRate(@PathVariable Long id) {
        log.debug("REST request to delete Rate : {}", id);
        rateService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /_search/rates?query=:query} : search for the rate corresponding
     * to the query.
     *
     * @param query the query of the rate search.
     * @return the result of the search.
     */
    @GetMapping("/_search/rates")
    public List<RateDTO> searchRates(@RequestParam String query) {
        log.debug("REST request to search Rates for query {}", query);
        return rateService.search(query);
    }
}
