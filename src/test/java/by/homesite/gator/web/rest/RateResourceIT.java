package by.homesite.gator.web.rest;

import static by.homesite.gator.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import by.homesite.gator.IntegrationTest;
import by.homesite.gator.domain.Rate;
import by.homesite.gator.repository.RateRepository;
import by.homesite.gator.repository.search.RateSearchRepository;
import by.homesite.gator.service.dto.RateDTO;
import by.homesite.gator.service.mapper.RateMapper;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link RateResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class RateResourceIT {

    private static final String DEFAULT_IDNAME = "AAAAAAAAAA";
    private static final String UPDATED_IDNAME = "BBBBBBBBBB";

    private static final String DEFAULT_CODE = "AAAAAAAAAA";
    private static final String UPDATED_CODE = "BBBBBBBBBB";

    private static final BigDecimal DEFAULT_RATE = new BigDecimal(1);
    private static final BigDecimal UPDATED_RATE = new BigDecimal(2);

    private static final Instant DEFAULT_ACTIVE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_ACTIVE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/rates";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/rates";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private RateRepository rateRepository;

    @Autowired
    private RateMapper rateMapper;

    /**
     * This repository is mocked in the by.homesite.gator.repository.search test package.
     *
     * @see by.homesite.gator.repository.search.RateSearchRepositoryMockConfiguration
     */
    @Autowired
    private RateSearchRepository mockRateSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restRateMockMvc;

    private Rate rate;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Rate createEntity(EntityManager em) {
        Rate rate = new Rate().idname(DEFAULT_IDNAME).code(DEFAULT_CODE).rate(DEFAULT_RATE).active(DEFAULT_ACTIVE);
        return rate;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Rate createUpdatedEntity(EntityManager em) {
        Rate rate = new Rate().idname(UPDATED_IDNAME).code(UPDATED_CODE).rate(UPDATED_RATE).active(UPDATED_ACTIVE);
        return rate;
    }

    @BeforeEach
    public void initTest() {
        rate = createEntity(em);
    }

    @Test
    @Transactional
    void createRate() throws Exception {
        int databaseSizeBeforeCreate = rateRepository.findAll().size();
        // Create the Rate
        RateDTO rateDTO = rateMapper.toDto(rate);
        restRateMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(rateDTO))
            )
            .andExpect(status().isCreated());

        // Validate the Rate in the database
        List<Rate> rateList = rateRepository.findAll();
        assertThat(rateList).hasSize(databaseSizeBeforeCreate + 1);
        Rate testRate = rateList.get(rateList.size() - 1);
        assertThat(testRate.getIdname()).isEqualTo(DEFAULT_IDNAME);
        assertThat(testRate.getCode()).isEqualTo(DEFAULT_CODE);
        assertThat(testRate.getRate()).isEqualByComparingTo(DEFAULT_RATE);
        assertThat(testRate.getActive()).isEqualTo(DEFAULT_ACTIVE);

        // Validate the Rate in Elasticsearch
        verify(mockRateSearchRepository, times(1)).save(testRate);
    }

    @Test
    @Transactional
    void createRateWithExistingId() throws Exception {
        // Create the Rate with an existing ID
        rate.setId(1L);
        RateDTO rateDTO = rateMapper.toDto(rate);

        int databaseSizeBeforeCreate = rateRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restRateMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(rateDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Rate in the database
        List<Rate> rateList = rateRepository.findAll();
        assertThat(rateList).hasSize(databaseSizeBeforeCreate);

        // Validate the Rate in Elasticsearch
        verify(mockRateSearchRepository, times(0)).save(rate);
    }

    @Test
    @Transactional
    void getAllRates() throws Exception {
        // Initialize the database
        rateRepository.saveAndFlush(rate);

        // Get all the rateList
        restRateMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(rate.getId().intValue())))
            .andExpect(jsonPath("$.[*].idname").value(hasItem(DEFAULT_IDNAME)))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE)))
            .andExpect(jsonPath("$.[*].rate").value(hasItem(sameNumber(DEFAULT_RATE))))
            .andExpect(jsonPath("$.[*].active").value(hasItem(DEFAULT_ACTIVE.toString())));
    }

    @Test
    @Transactional
    void getRate() throws Exception {
        // Initialize the database
        rateRepository.saveAndFlush(rate);

        // Get the rate
        restRateMockMvc
            .perform(get(ENTITY_API_URL_ID, rate.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(rate.getId().intValue()))
            .andExpect(jsonPath("$.idname").value(DEFAULT_IDNAME))
            .andExpect(jsonPath("$.code").value(DEFAULT_CODE))
            .andExpect(jsonPath("$.rate").value(sameNumber(DEFAULT_RATE)))
            .andExpect(jsonPath("$.active").value(DEFAULT_ACTIVE.toString()));
    }

    @Test
    @Transactional
    void getNonExistingRate() throws Exception {
        // Get the rate
        restRateMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewRate() throws Exception {
        // Initialize the database
        rateRepository.saveAndFlush(rate);

        int databaseSizeBeforeUpdate = rateRepository.findAll().size();

        // Update the rate
        Rate updatedRate = rateRepository.findById(rate.getId()).get();
        // Disconnect from session so that the updates on updatedRate are not directly saved in db
        em.detach(updatedRate);
        updatedRate.idname(UPDATED_IDNAME).code(UPDATED_CODE).rate(UPDATED_RATE).active(UPDATED_ACTIVE);
        RateDTO rateDTO = rateMapper.toDto(updatedRate);

        restRateMockMvc
            .perform(
                put(ENTITY_API_URL_ID, rateDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(rateDTO))
            )
            .andExpect(status().isOk());

        // Validate the Rate in the database
        List<Rate> rateList = rateRepository.findAll();
        assertThat(rateList).hasSize(databaseSizeBeforeUpdate);
        Rate testRate = rateList.get(rateList.size() - 1);
        assertThat(testRate.getIdname()).isEqualTo(UPDATED_IDNAME);
        assertThat(testRate.getCode()).isEqualTo(UPDATED_CODE);
        assertThat(testRate.getRate()).isEqualTo(UPDATED_RATE);
        assertThat(testRate.getActive()).isEqualTo(UPDATED_ACTIVE);

        // Validate the Rate in Elasticsearch
        verify(mockRateSearchRepository).save(testRate);
    }

    @Test
    @Transactional
    void putNonExistingRate() throws Exception {
        int databaseSizeBeforeUpdate = rateRepository.findAll().size();
        rate.setId(count.incrementAndGet());

        // Create the Rate
        RateDTO rateDTO = rateMapper.toDto(rate);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restRateMockMvc
            .perform(
                put(ENTITY_API_URL_ID, rateDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(rateDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Rate in the database
        List<Rate> rateList = rateRepository.findAll();
        assertThat(rateList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Rate in Elasticsearch
        verify(mockRateSearchRepository, times(0)).save(rate);
    }

    @Test
    @Transactional
    void putWithIdMismatchRate() throws Exception {
        int databaseSizeBeforeUpdate = rateRepository.findAll().size();
        rate.setId(count.incrementAndGet());

        // Create the Rate
        RateDTO rateDTO = rateMapper.toDto(rate);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRateMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(rateDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Rate in the database
        List<Rate> rateList = rateRepository.findAll();
        assertThat(rateList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Rate in Elasticsearch
        verify(mockRateSearchRepository, times(0)).save(rate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamRate() throws Exception {
        int databaseSizeBeforeUpdate = rateRepository.findAll().size();
        rate.setId(count.incrementAndGet());

        // Create the Rate
        RateDTO rateDTO = rateMapper.toDto(rate);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRateMockMvc
            .perform(
                put(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(rateDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Rate in the database
        List<Rate> rateList = rateRepository.findAll();
        assertThat(rateList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Rate in Elasticsearch
        verify(mockRateSearchRepository, times(0)).save(rate);
    }

    @Test
    @Transactional
    void partialUpdateRateWithPatch() throws Exception {
        // Initialize the database
        rateRepository.saveAndFlush(rate);

        int databaseSizeBeforeUpdate = rateRepository.findAll().size();

        // Update the rate using partial update
        Rate partialUpdatedRate = new Rate();
        partialUpdatedRate.setId(rate.getId());

        partialUpdatedRate.rate(UPDATED_RATE);

        restRateMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedRate.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedRate))
            )
            .andExpect(status().isOk());

        // Validate the Rate in the database
        List<Rate> rateList = rateRepository.findAll();
        assertThat(rateList).hasSize(databaseSizeBeforeUpdate);
        Rate testRate = rateList.get(rateList.size() - 1);
        assertThat(testRate.getIdname()).isEqualTo(DEFAULT_IDNAME);
        assertThat(testRate.getCode()).isEqualTo(DEFAULT_CODE);
        assertThat(testRate.getRate()).isEqualByComparingTo(UPDATED_RATE);
        assertThat(testRate.getActive()).isEqualTo(DEFAULT_ACTIVE);
    }

    @Test
    @Transactional
    void fullUpdateRateWithPatch() throws Exception {
        // Initialize the database
        rateRepository.saveAndFlush(rate);

        int databaseSizeBeforeUpdate = rateRepository.findAll().size();

        // Update the rate using partial update
        Rate partialUpdatedRate = new Rate();
        partialUpdatedRate.setId(rate.getId());

        partialUpdatedRate.idname(UPDATED_IDNAME).code(UPDATED_CODE).rate(UPDATED_RATE).active(UPDATED_ACTIVE);

        restRateMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedRate.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedRate))
            )
            .andExpect(status().isOk());

        // Validate the Rate in the database
        List<Rate> rateList = rateRepository.findAll();
        assertThat(rateList).hasSize(databaseSizeBeforeUpdate);
        Rate testRate = rateList.get(rateList.size() - 1);
        assertThat(testRate.getIdname()).isEqualTo(UPDATED_IDNAME);
        assertThat(testRate.getCode()).isEqualTo(UPDATED_CODE);
        assertThat(testRate.getRate()).isEqualByComparingTo(UPDATED_RATE);
        assertThat(testRate.getActive()).isEqualTo(UPDATED_ACTIVE);
    }

    @Test
    @Transactional
    void patchNonExistingRate() throws Exception {
        int databaseSizeBeforeUpdate = rateRepository.findAll().size();
        rate.setId(count.incrementAndGet());

        // Create the Rate
        RateDTO rateDTO = rateMapper.toDto(rate);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restRateMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, rateDTO.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(rateDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Rate in the database
        List<Rate> rateList = rateRepository.findAll();
        assertThat(rateList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Rate in Elasticsearch
        verify(mockRateSearchRepository, times(0)).save(rate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchRate() throws Exception {
        int databaseSizeBeforeUpdate = rateRepository.findAll().size();
        rate.setId(count.incrementAndGet());

        // Create the Rate
        RateDTO rateDTO = rateMapper.toDto(rate);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRateMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(rateDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Rate in the database
        List<Rate> rateList = rateRepository.findAll();
        assertThat(rateList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Rate in Elasticsearch
        verify(mockRateSearchRepository, times(0)).save(rate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamRate() throws Exception {
        int databaseSizeBeforeUpdate = rateRepository.findAll().size();
        rate.setId(count.incrementAndGet());

        // Create the Rate
        RateDTO rateDTO = rateMapper.toDto(rate);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRateMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(rateDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Rate in the database
        List<Rate> rateList = rateRepository.findAll();
        assertThat(rateList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Rate in Elasticsearch
        verify(mockRateSearchRepository, times(0)).save(rate);
    }

    @Test
    @Transactional
    void deleteRate() throws Exception {
        // Initialize the database
        rateRepository.saveAndFlush(rate);

        int databaseSizeBeforeDelete = rateRepository.findAll().size();

        // Delete the rate
        restRateMockMvc
            .perform(delete(ENTITY_API_URL_ID, rate.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Rate> rateList = rateRepository.findAll();
        assertThat(rateList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the Rate in Elasticsearch
        verify(mockRateSearchRepository, times(1)).deleteById(rate.getId());
    }

    @Test
    @Transactional
    void searchRate() throws Exception {
        // Configure the mock search repository
        // Initialize the database
        rateRepository.saveAndFlush(rate);
        when(mockRateSearchRepository.search(queryStringQuery("id:" + rate.getId()))).thenReturn(Collections.singletonList(rate));

        // Search the rate
        restRateMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + rate.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(rate.getId().intValue())))
            .andExpect(jsonPath("$.[*].idname").value(hasItem(DEFAULT_IDNAME)))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE)))
            .andExpect(jsonPath("$.[*].rate").value(hasItem(sameNumber(DEFAULT_RATE))))
            .andExpect(jsonPath("$.[*].active").value(hasItem(DEFAULT_ACTIVE.toString())));
    }
}
