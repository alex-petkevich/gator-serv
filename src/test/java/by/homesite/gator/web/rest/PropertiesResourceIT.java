package by.homesite.gator.web.rest;

import by.homesite.gator.GatorApp;
import by.homesite.gator.domain.Properties;
import by.homesite.gator.repository.PropertiesRepository;
import by.homesite.gator.repository.search.PropertiesSearchRepository;
import by.homesite.gator.service.PropertiesService;
import by.homesite.gator.service.dto.PropertiesDTO;
import by.homesite.gator.service.mapper.PropertiesMapper;
import by.homesite.gator.web.rest.errors.ExceptionTranslator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Validator;

import javax.persistence.EntityManager;
import java.util.Collections;
import java.util.List;

import static by.homesite.gator.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the {@link PropertiesResource} REST controller.
 */
@SpringBootTest(classes = GatorApp.class)
public class PropertiesResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final Boolean DEFAULT_IS_ACTIVE = false;
    private static final Boolean UPDATED_IS_ACTIVE = true;

    @Autowired
    private PropertiesRepository propertiesRepository;

    @Autowired
    private PropertiesMapper propertiesMapper;

    @Autowired
    private PropertiesService propertiesService;

    /**
     * This repository is mocked in the by.homesite.gator.repository.search test package.
     *
     * @see by.homesite.gator.repository.search.PropertiesSearchRepositoryMockConfiguration
     */
    @Autowired
    private PropertiesSearchRepository mockPropertiesSearchRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    @Autowired
    private Validator validator;

    private MockMvc restPropertiesMockMvc;

    private Properties properties;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final PropertiesResource propertiesResource = new PropertiesResource(propertiesService);
        this.restPropertiesMockMvc = MockMvcBuilders.standaloneSetup(propertiesResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter)
            .setValidator(validator).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Properties createEntity(EntityManager em) {
        Properties properties = new Properties()
            .name(DEFAULT_NAME)
            .isActive(DEFAULT_IS_ACTIVE);
        return properties;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Properties createUpdatedEntity(EntityManager em) {
        Properties properties = new Properties()
            .name(UPDATED_NAME)
            .isActive(UPDATED_IS_ACTIVE);
        return properties;
    }

    @BeforeEach
    public void initTest() {
        properties = createEntity(em);
    }

    @Test
    @Transactional
    public void createProperties() throws Exception {
        int databaseSizeBeforeCreate = propertiesRepository.findAll().size();

        // Create the Properties
        PropertiesDTO propertiesDTO = propertiesMapper.toDto(properties);
        restPropertiesMockMvc.perform(post("/api/properties")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(propertiesDTO)))
            .andExpect(status().isCreated());

        // Validate the Properties in the database
        List<Properties> propertiesList = propertiesRepository.findAll();
        assertThat(propertiesList).hasSize(databaseSizeBeforeCreate + 1);
        Properties testProperties = propertiesList.get(propertiesList.size() - 1);
        assertThat(testProperties.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testProperties.isIsActive()).isEqualTo(DEFAULT_IS_ACTIVE);

        // Validate the Properties in Elasticsearch
        verify(mockPropertiesSearchRepository, times(1)).save(testProperties);
    }

    @Test
    @Transactional
    public void createPropertiesWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = propertiesRepository.findAll().size();

        // Create the Properties with an existing ID
        properties.setId(1L);
        PropertiesDTO propertiesDTO = propertiesMapper.toDto(properties);

        // An entity with an existing ID cannot be created, so this API call must fail
        restPropertiesMockMvc.perform(post("/api/properties")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(propertiesDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Properties in the database
        List<Properties> propertiesList = propertiesRepository.findAll();
        assertThat(propertiesList).hasSize(databaseSizeBeforeCreate);

        // Validate the Properties in Elasticsearch
        verify(mockPropertiesSearchRepository, times(0)).save(properties);
    }


    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = propertiesRepository.findAll().size();
        // set the field null
        properties.setName(null);

        // Create the Properties, which fails.
        PropertiesDTO propertiesDTO = propertiesMapper.toDto(properties);

        restPropertiesMockMvc.perform(post("/api/properties")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(propertiesDTO)))
            .andExpect(status().isBadRequest());

        List<Properties> propertiesList = propertiesRepository.findAll();
        assertThat(propertiesList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllProperties() throws Exception {
        // Initialize the database
        propertiesRepository.saveAndFlush(properties);

        // Get all the propertiesList
        restPropertiesMockMvc.perform(get("/api/properties?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(properties.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].isActive").value(hasItem(DEFAULT_IS_ACTIVE.booleanValue())));
    }
    
    @Test
    @Transactional
    public void getProperties() throws Exception {
        // Initialize the database
        propertiesRepository.saveAndFlush(properties);

        // Get the properties
        restPropertiesMockMvc.perform(get("/api/properties/{id}", properties.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(properties.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.isActive").value(DEFAULT_IS_ACTIVE.booleanValue()));
    }

    @Test
    @Transactional
    public void getNonExistingProperties() throws Exception {
        // Get the properties
        restPropertiesMockMvc.perform(get("/api/properties/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateProperties() throws Exception {
        // Initialize the database
        propertiesRepository.saveAndFlush(properties);

        int databaseSizeBeforeUpdate = propertiesRepository.findAll().size();

        // Update the properties
        Properties updatedProperties = propertiesRepository.findById(properties.getId()).get();
        // Disconnect from session so that the updates on updatedProperties are not directly saved in db
        em.detach(updatedProperties);
        updatedProperties
            .name(UPDATED_NAME)
            .isActive(UPDATED_IS_ACTIVE);
        PropertiesDTO propertiesDTO = propertiesMapper.toDto(updatedProperties);

        restPropertiesMockMvc.perform(put("/api/properties")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(propertiesDTO)))
            .andExpect(status().isOk());

        // Validate the Properties in the database
        List<Properties> propertiesList = propertiesRepository.findAll();
        assertThat(propertiesList).hasSize(databaseSizeBeforeUpdate);
        Properties testProperties = propertiesList.get(propertiesList.size() - 1);
        assertThat(testProperties.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testProperties.isIsActive()).isEqualTo(UPDATED_IS_ACTIVE);

        // Validate the Properties in Elasticsearch
        verify(mockPropertiesSearchRepository, times(1)).save(testProperties);
    }

    @Test
    @Transactional
    public void updateNonExistingProperties() throws Exception {
        int databaseSizeBeforeUpdate = propertiesRepository.findAll().size();

        // Create the Properties
        PropertiesDTO propertiesDTO = propertiesMapper.toDto(properties);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPropertiesMockMvc.perform(put("/api/properties")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(propertiesDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Properties in the database
        List<Properties> propertiesList = propertiesRepository.findAll();
        assertThat(propertiesList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Properties in Elasticsearch
        verify(mockPropertiesSearchRepository, times(0)).save(properties);
    }

    @Test
    @Transactional
    public void deleteProperties() throws Exception {
        // Initialize the database
        propertiesRepository.saveAndFlush(properties);

        int databaseSizeBeforeDelete = propertiesRepository.findAll().size();

        // Delete the properties
        restPropertiesMockMvc.perform(delete("/api/properties/{id}", properties.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Properties> propertiesList = propertiesRepository.findAll();
        assertThat(propertiesList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the Properties in Elasticsearch
        verify(mockPropertiesSearchRepository, times(1)).deleteById(properties.getId());
    }

    @Test
    @Transactional
    public void searchProperties() throws Exception {
        // Initialize the database
        propertiesRepository.saveAndFlush(properties);
        when(mockPropertiesSearchRepository.search(queryStringQuery("id:" + properties.getId())))
            .thenReturn(Collections.singletonList(properties));
        // Search the properties
        restPropertiesMockMvc.perform(get("/api/_search/properties?query=id:" + properties.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(properties.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].isActive").value(hasItem(DEFAULT_IS_ACTIVE.booleanValue())));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Properties.class);
        Properties properties1 = new Properties();
        properties1.setId(1L);
        Properties properties2 = new Properties();
        properties2.setId(properties1.getId());
        assertThat(properties1).isEqualTo(properties2);
        properties2.setId(2L);
        assertThat(properties1).isNotEqualTo(properties2);
        properties1.setId(null);
        assertThat(properties1).isNotEqualTo(properties2);
    }

    @Test
    @Transactional
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(PropertiesDTO.class);
        PropertiesDTO propertiesDTO1 = new PropertiesDTO();
        propertiesDTO1.setId(1L);
        PropertiesDTO propertiesDTO2 = new PropertiesDTO();
        assertThat(propertiesDTO1).isNotEqualTo(propertiesDTO2);
        propertiesDTO2.setId(propertiesDTO1.getId());
        assertThat(propertiesDTO1).isEqualTo(propertiesDTO2);
        propertiesDTO2.setId(2L);
        assertThat(propertiesDTO1).isNotEqualTo(propertiesDTO2);
        propertiesDTO1.setId(null);
        assertThat(propertiesDTO1).isNotEqualTo(propertiesDTO2);
    }

    @Test
    @Transactional
    public void testEntityFromId() {
        assertThat(propertiesMapper.fromId(42L).getId()).isEqualTo(42);
        assertThat(propertiesMapper.fromId(null)).isNull();
    }
}
