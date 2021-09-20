package by.homesite.gator.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import by.homesite.gator.IntegrationTest;
import by.homesite.gator.domain.Properties;
import by.homesite.gator.repository.PropertiesRepository;
import by.homesite.gator.repository.search.PropertiesSearchRepository;
import by.homesite.gator.service.dto.PropertiesDTO;
import by.homesite.gator.service.mapper.PropertiesMapper;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link PropertiesResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class PropertiesResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final Boolean DEFAULT_IS_ACTIVE = false;
    private static final Boolean UPDATED_IS_ACTIVE = true;

    private static final String ENTITY_API_URL = "/api/properties";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/properties";

    private static final Random random = new Random();
    private static final AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private PropertiesRepository propertiesRepository;

    @Autowired
    private PropertiesMapper propertiesMapper;

    /**
     * This repository is mocked in the by.homesite.gator.repository.search test package.
     *
     * @see by.homesite.gator.repository.search.PropertiesSearchRepositoryMockConfiguration
     */
    @Autowired
    private PropertiesSearchRepository mockPropertiesSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restPropertiesMockMvc;

    private Properties properties;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Properties createEntity(EntityManager em) {
        Properties properties = new Properties().name(DEFAULT_NAME).isActive(DEFAULT_IS_ACTIVE);
        return properties;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Properties createUpdatedEntity(EntityManager em) {
        Properties properties = new Properties().name(UPDATED_NAME).isActive(UPDATED_IS_ACTIVE);
        return properties;
    }

    @BeforeEach
    public void initTest() {
        properties = createEntity(em);
    }

    @Test
    @Transactional
    void createProperties() throws Exception {
        int databaseSizeBeforeCreate = propertiesRepository.findAll().size();
        // Create the Properties
        PropertiesDTO propertiesDTO = propertiesMapper.toDto(properties);
        restPropertiesMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(propertiesDTO))
            )
            .andExpect(status().isCreated());

        // Validate the Properties in the database
        List<Properties> propertiesList = propertiesRepository.findAll();
        assertThat(propertiesList).hasSize(databaseSizeBeforeCreate + 1);
        Properties testProperties = propertiesList.get(propertiesList.size() - 1);
        assertThat(testProperties.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testProperties.getIsActive()).isEqualTo(DEFAULT_IS_ACTIVE);

        // Validate the Properties in Elasticsearch
        verify(mockPropertiesSearchRepository, times(1)).save(testProperties);
    }

    @Test
    @Transactional
    void createPropertiesWithExistingId() throws Exception {
        // Create the Properties with an existing ID
        properties.setId(1L);
        PropertiesDTO propertiesDTO = propertiesMapper.toDto(properties);

        int databaseSizeBeforeCreate = propertiesRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restPropertiesMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(propertiesDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Properties in the database
        List<Properties> propertiesList = propertiesRepository.findAll();
        assertThat(propertiesList).hasSize(databaseSizeBeforeCreate);

        // Validate the Properties in Elasticsearch
        verify(mockPropertiesSearchRepository, times(0)).save(properties);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = propertiesRepository.findAll().size();
        // set the field null
        properties.setName(null);

        // Create the Properties, which fails.
        PropertiesDTO propertiesDTO = propertiesMapper.toDto(properties);

        restPropertiesMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(propertiesDTO))
            )
            .andExpect(status().isBadRequest());

        List<Properties> propertiesList = propertiesRepository.findAll();
        assertThat(propertiesList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllProperties() throws Exception {
        // Initialize the database
        propertiesRepository.saveAndFlush(properties);

        // Get all the propertiesList
        restPropertiesMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(properties.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].isActive").value(hasItem(DEFAULT_IS_ACTIVE)));
    }

    @Test
    @Transactional
    void getProperties() throws Exception {
        // Initialize the database
        propertiesRepository.saveAndFlush(properties);

        // Get the properties
        restPropertiesMockMvc
            .perform(get(ENTITY_API_URL_ID, properties.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(properties.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.isActive").value(DEFAULT_IS_ACTIVE));
    }

    @Test
    @Transactional
    void getNonExistingProperties() throws Exception {
        // Get the properties
        restPropertiesMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewProperties() throws Exception {
        // Initialize the database
        propertiesRepository.saveAndFlush(properties);

        int databaseSizeBeforeUpdate = propertiesRepository.findAll().size();

        // Update the properties
        Properties updatedProperties = propertiesRepository.findById(properties.getId()).get();
        // Disconnect from session so that the updates on updatedProperties are not directly saved in db
        em.detach(updatedProperties);
        updatedProperties.name(UPDATED_NAME).isActive(UPDATED_IS_ACTIVE);
        PropertiesDTO propertiesDTO = propertiesMapper.toDto(updatedProperties);

        restPropertiesMockMvc
            .perform(
                put(ENTITY_API_URL_ID, propertiesDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(propertiesDTO))
            )
            .andExpect(status().isOk());

        // Validate the Properties in the database
        List<Properties> propertiesList = propertiesRepository.findAll();
        assertThat(propertiesList).hasSize(databaseSizeBeforeUpdate);
        Properties testProperties = propertiesList.get(propertiesList.size() - 1);
        assertThat(testProperties.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testProperties.getIsActive()).isEqualTo(UPDATED_IS_ACTIVE);

        // Validate the Properties in Elasticsearch
        verify(mockPropertiesSearchRepository).save(testProperties);
    }

    @Test
    @Transactional
    void putNonExistingProperties() throws Exception {
        int databaseSizeBeforeUpdate = propertiesRepository.findAll().size();
        properties.setId(count.incrementAndGet());

        // Create the Properties
        PropertiesDTO propertiesDTO = propertiesMapper.toDto(properties);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPropertiesMockMvc
            .perform(
                put(ENTITY_API_URL_ID, propertiesDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(propertiesDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Properties in the database
        List<Properties> propertiesList = propertiesRepository.findAll();
        assertThat(propertiesList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Properties in Elasticsearch
        verify(mockPropertiesSearchRepository, times(0)).save(properties);
    }

    @Test
    @Transactional
    void putWithIdMismatchProperties() throws Exception {
        int databaseSizeBeforeUpdate = propertiesRepository.findAll().size();
        properties.setId(count.incrementAndGet());

        // Create the Properties
        PropertiesDTO propertiesDTO = propertiesMapper.toDto(properties);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPropertiesMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(propertiesDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Properties in the database
        List<Properties> propertiesList = propertiesRepository.findAll();
        assertThat(propertiesList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Properties in Elasticsearch
        verify(mockPropertiesSearchRepository, times(0)).save(properties);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamProperties() throws Exception {
        int databaseSizeBeforeUpdate = propertiesRepository.findAll().size();
        properties.setId(count.incrementAndGet());

        // Create the Properties
        PropertiesDTO propertiesDTO = propertiesMapper.toDto(properties);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPropertiesMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(propertiesDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Properties in the database
        List<Properties> propertiesList = propertiesRepository.findAll();
        assertThat(propertiesList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Properties in Elasticsearch
        verify(mockPropertiesSearchRepository, times(0)).save(properties);
    }

    @Test
    @Transactional
    void partialUpdatePropertiesWithPatch() throws Exception {
        // Initialize the database
        propertiesRepository.saveAndFlush(properties);

        int databaseSizeBeforeUpdate = propertiesRepository.findAll().size();

        // Update the properties using partial update
        Properties partialUpdatedProperties = new Properties();
        partialUpdatedProperties.setId(properties.getId());

        restPropertiesMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedProperties.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedProperties))
            )
            .andExpect(status().isOk());

        // Validate the Properties in the database
        List<Properties> propertiesList = propertiesRepository.findAll();
        assertThat(propertiesList).hasSize(databaseSizeBeforeUpdate);
        Properties testProperties = propertiesList.get(propertiesList.size() - 1);
        assertThat(testProperties.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testProperties.getIsActive()).isEqualTo(DEFAULT_IS_ACTIVE);
    }

    @Test
    @Transactional
    void fullUpdatePropertiesWithPatch() throws Exception {
        // Initialize the database
        propertiesRepository.saveAndFlush(properties);

        int databaseSizeBeforeUpdate = propertiesRepository.findAll().size();

        // Update the properties using partial update
        Properties partialUpdatedProperties = new Properties();
        partialUpdatedProperties.setId(properties.getId());

        partialUpdatedProperties.name(UPDATED_NAME).isActive(UPDATED_IS_ACTIVE);

        restPropertiesMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedProperties.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedProperties))
            )
            .andExpect(status().isOk());

        // Validate the Properties in the database
        List<Properties> propertiesList = propertiesRepository.findAll();
        assertThat(propertiesList).hasSize(databaseSizeBeforeUpdate);
        Properties testProperties = propertiesList.get(propertiesList.size() - 1);
        assertThat(testProperties.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testProperties.getIsActive()).isEqualTo(UPDATED_IS_ACTIVE);
    }

    @Test
    @Transactional
    void patchNonExistingProperties() throws Exception {
        int databaseSizeBeforeUpdate = propertiesRepository.findAll().size();
        properties.setId(count.incrementAndGet());

        // Create the Properties
        PropertiesDTO propertiesDTO = propertiesMapper.toDto(properties);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPropertiesMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, propertiesDTO.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(propertiesDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Properties in the database
        List<Properties> propertiesList = propertiesRepository.findAll();
        assertThat(propertiesList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Properties in Elasticsearch
        verify(mockPropertiesSearchRepository, times(0)).save(properties);
    }

    @Test
    @Transactional
    void patchWithIdMismatchProperties() throws Exception {
        int databaseSizeBeforeUpdate = propertiesRepository.findAll().size();
        properties.setId(count.incrementAndGet());

        // Create the Properties
        PropertiesDTO propertiesDTO = propertiesMapper.toDto(properties);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPropertiesMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(propertiesDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Properties in the database
        List<Properties> propertiesList = propertiesRepository.findAll();
        assertThat(propertiesList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Properties in Elasticsearch
        verify(mockPropertiesSearchRepository, times(0)).save(properties);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamProperties() throws Exception {
        int databaseSizeBeforeUpdate = propertiesRepository.findAll().size();
        properties.setId(count.incrementAndGet());

        // Create the Properties
        PropertiesDTO propertiesDTO = propertiesMapper.toDto(properties);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPropertiesMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(propertiesDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Properties in the database
        List<Properties> propertiesList = propertiesRepository.findAll();
        assertThat(propertiesList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Properties in Elasticsearch
        verify(mockPropertiesSearchRepository, times(0)).save(properties);
    }

    @Test
    @Transactional
    void deleteProperties() throws Exception {
        // Initialize the database
        propertiesRepository.saveAndFlush(properties);

        int databaseSizeBeforeDelete = propertiesRepository.findAll().size();

        // Delete the properties
        restPropertiesMockMvc
            .perform(delete(ENTITY_API_URL_ID, properties.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Properties> propertiesList = propertiesRepository.findAll();
        assertThat(propertiesList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the Properties in Elasticsearch
        verify(mockPropertiesSearchRepository, times(1)).deleteById(properties.getId());
    }

    @Test
    @Transactional
    void searchProperties() throws Exception {
        // Configure the mock search repository
        // Initialize the database
        propertiesRepository.saveAndFlush(properties);
        when(mockPropertiesSearchRepository.search(queryStringQuery("id:" + properties.getId())))
            .thenReturn(Collections.singletonList(properties));

        // Search the properties
        restPropertiesMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + properties.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(properties.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].isActive").value(hasItem(DEFAULT_IS_ACTIVE)));
    }
}
