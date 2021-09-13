package by.homesite.gator.web.rest;

import by.homesite.gator.GatorApp;
import by.homesite.gator.domain.UserProperties;
import by.homesite.gator.repository.UserPropertiesRepository;
import by.homesite.gator.repository.search.UserPropertiesSearchRepository;
import by.homesite.gator.service.UserPropertiesService;
import by.homesite.gator.service.dto.UserPropertiesDTO;
import by.homesite.gator.service.mapper.UserPropertiesMapper;
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
 * Integration tests for the {@link UserPropertiesResource} REST controller.
 */
@SpringBootTest(classes = GatorApp.class)
public class UserPropertiesResourceIT {

    private static final String DEFAULT_VALUE = "AAAAAAAAAA";
    private static final String UPDATED_VALUE = "BBBBBBBBBB";

    @Autowired
    private UserPropertiesRepository userPropertiesRepository;

    @Autowired
    private UserPropertiesMapper userPropertiesMapper;

    @Autowired
    private UserPropertiesService userPropertiesService;

    /**
     * This repository is mocked in the by.homesite.gator.repository.search test package.
     *
     * @see by.homesite.gator.repository.search.UserPropertiesSearchRepositoryMockConfiguration
     */
    @Autowired
    private UserPropertiesSearchRepository mockUserPropertiesSearchRepository;

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

    private MockMvc restUserPropertiesMockMvc;

    private UserProperties userProperties;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final UserPropertiesResource userPropertiesResource = new UserPropertiesResource(userPropertiesService);
        this.restUserPropertiesMockMvc = MockMvcBuilders.standaloneSetup(userPropertiesResource)
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
    public static UserProperties createEntity(EntityManager em) {
        UserProperties userProperties = new UserProperties()
            .value(DEFAULT_VALUE);
        return userProperties;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static UserProperties createUpdatedEntity(EntityManager em) {
        UserProperties userProperties = new UserProperties()
            .value(UPDATED_VALUE);
        return userProperties;
    }

    @BeforeEach
    public void initTest() {
        userProperties = createEntity(em);
    }

    @Test
    @Transactional
    public void createUserProperties() throws Exception {
        int databaseSizeBeforeCreate = userPropertiesRepository.findAll().size();

        // Create the UserProperties
        UserPropertiesDTO userPropertiesDTO = userPropertiesMapper.toDto(userProperties);
        restUserPropertiesMockMvc.perform(post("/api/user-properties")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(userPropertiesDTO)))
            .andExpect(status().isCreated());

        // Validate the UserProperties in the database
        List<UserProperties> userPropertiesList = userPropertiesRepository.findAll();
        assertThat(userPropertiesList).hasSize(databaseSizeBeforeCreate + 1);
        UserProperties testUserProperties = userPropertiesList.get(userPropertiesList.size() - 1);
        assertThat(testUserProperties.getValue()).isEqualTo(DEFAULT_VALUE);

        // Validate the UserProperties in Elasticsearch
        verify(mockUserPropertiesSearchRepository, times(1)).save(testUserProperties);
    }

    @Test
    @Transactional
    public void createUserPropertiesWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = userPropertiesRepository.findAll().size();

        // Create the UserProperties with an existing ID
        userProperties.setId(1L);
        UserPropertiesDTO userPropertiesDTO = userPropertiesMapper.toDto(userProperties);

        // An entity with an existing ID cannot be created, so this API call must fail
        restUserPropertiesMockMvc.perform(post("/api/user-properties")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(userPropertiesDTO)))
            .andExpect(status().isBadRequest());

        // Validate the UserProperties in the database
        List<UserProperties> userPropertiesList = userPropertiesRepository.findAll();
        assertThat(userPropertiesList).hasSize(databaseSizeBeforeCreate);

        // Validate the UserProperties in Elasticsearch
        verify(mockUserPropertiesSearchRepository, times(0)).save(userProperties);
    }


    @Test
    @Transactional
    public void getAllUserProperties() throws Exception {
        // Initialize the database
        userPropertiesRepository.saveAndFlush(userProperties);

        // Get all the userPropertiesList
        restUserPropertiesMockMvc.perform(get("/api/user-properties?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(userProperties.getId().intValue())))
            .andExpect(jsonPath("$.[*].value").value(hasItem(DEFAULT_VALUE.toString())));
    }
    
    @Test
    @Transactional
    public void getUserProperties() throws Exception {
        // Initialize the database
        userPropertiesRepository.saveAndFlush(userProperties);

        // Get the userProperties
        restUserPropertiesMockMvc.perform(get("/api/user-properties/{id}", userProperties.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(userProperties.getId().intValue()))
            .andExpect(jsonPath("$.value").value(DEFAULT_VALUE.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingUserProperties() throws Exception {
        // Get the userProperties
        restUserPropertiesMockMvc.perform(get("/api/user-properties/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateUserProperties() throws Exception {
        // Initialize the database
        userPropertiesRepository.saveAndFlush(userProperties);

        int databaseSizeBeforeUpdate = userPropertiesRepository.findAll().size();

        // Update the userProperties
        UserProperties updatedUserProperties = userPropertiesRepository.findById(userProperties.getId()).get();
        // Disconnect from session so that the updates on updatedUserProperties are not directly saved in db
        em.detach(updatedUserProperties);
        updatedUserProperties
            .value(UPDATED_VALUE);
        UserPropertiesDTO userPropertiesDTO = userPropertiesMapper.toDto(updatedUserProperties);

        restUserPropertiesMockMvc.perform(put("/api/user-properties")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(userPropertiesDTO)))
            .andExpect(status().isOk());

        // Validate the UserProperties in the database
        List<UserProperties> userPropertiesList = userPropertiesRepository.findAll();
        assertThat(userPropertiesList).hasSize(databaseSizeBeforeUpdate);
        UserProperties testUserProperties = userPropertiesList.get(userPropertiesList.size() - 1);
        assertThat(testUserProperties.getValue()).isEqualTo(UPDATED_VALUE);

        // Validate the UserProperties in Elasticsearch
        verify(mockUserPropertiesSearchRepository, times(1)).save(testUserProperties);
    }

    @Test
    @Transactional
    public void updateNonExistingUserProperties() throws Exception {
        int databaseSizeBeforeUpdate = userPropertiesRepository.findAll().size();

        // Create the UserProperties
        UserPropertiesDTO userPropertiesDTO = userPropertiesMapper.toDto(userProperties);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restUserPropertiesMockMvc.perform(put("/api/user-properties")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(userPropertiesDTO)))
            .andExpect(status().isBadRequest());

        // Validate the UserProperties in the database
        List<UserProperties> userPropertiesList = userPropertiesRepository.findAll();
        assertThat(userPropertiesList).hasSize(databaseSizeBeforeUpdate);

        // Validate the UserProperties in Elasticsearch
        verify(mockUserPropertiesSearchRepository, times(0)).save(userProperties);
    }

    @Test
    @Transactional
    public void deleteUserProperties() throws Exception {
        // Initialize the database
        userPropertiesRepository.saveAndFlush(userProperties);

        int databaseSizeBeforeDelete = userPropertiesRepository.findAll().size();

        // Delete the userProperties
        restUserPropertiesMockMvc.perform(delete("/api/user-properties/{id}", userProperties.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<UserProperties> userPropertiesList = userPropertiesRepository.findAll();
        assertThat(userPropertiesList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the UserProperties in Elasticsearch
        verify(mockUserPropertiesSearchRepository, times(1)).deleteById(userProperties.getId());
    }

    @Test
    @Transactional
    public void searchUserProperties() throws Exception {
        // Initialize the database
        userPropertiesRepository.saveAndFlush(userProperties);
        when(mockUserPropertiesSearchRepository.search(queryStringQuery("id:" + userProperties.getId())))
            .thenReturn(Collections.singletonList(userProperties));
        // Search the userProperties
        restUserPropertiesMockMvc.perform(get("/api/_search/user-properties?query=id:" + userProperties.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(userProperties.getId().intValue())))
            .andExpect(jsonPath("$.[*].value").value(hasItem(DEFAULT_VALUE)));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(UserProperties.class);
        UserProperties userProperties1 = new UserProperties();
        userProperties1.setId(1L);
        UserProperties userProperties2 = new UserProperties();
        userProperties2.setId(userProperties1.getId());
        assertThat(userProperties1).isEqualTo(userProperties2);
        userProperties2.setId(2L);
        assertThat(userProperties1).isNotEqualTo(userProperties2);
        userProperties1.setId(null);
        assertThat(userProperties1).isNotEqualTo(userProperties2);
    }

    @Test
    @Transactional
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(UserPropertiesDTO.class);
        UserPropertiesDTO userPropertiesDTO1 = new UserPropertiesDTO();
        userPropertiesDTO1.setId(1L);
        UserPropertiesDTO userPropertiesDTO2 = new UserPropertiesDTO();
        assertThat(userPropertiesDTO1).isNotEqualTo(userPropertiesDTO2);
        userPropertiesDTO2.setId(userPropertiesDTO1.getId());
        assertThat(userPropertiesDTO1).isEqualTo(userPropertiesDTO2);
        userPropertiesDTO2.setId(2L);
        assertThat(userPropertiesDTO1).isNotEqualTo(userPropertiesDTO2);
        userPropertiesDTO1.setId(null);
        assertThat(userPropertiesDTO1).isNotEqualTo(userPropertiesDTO2);
    }

    @Test
    @Transactional
    public void testEntityFromId() {
        assertThat(userPropertiesMapper.fromId(42L).getId()).isEqualTo(42);
        assertThat(userPropertiesMapper.fromId(null)).isNull();
    }
}
