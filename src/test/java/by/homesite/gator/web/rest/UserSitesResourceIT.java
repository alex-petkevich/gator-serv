package by.homesite.gator.web.rest;

import by.homesite.gator.GatorApp;
import by.homesite.gator.domain.UserSites;
import by.homesite.gator.repository.UserSitesRepository;
import by.homesite.gator.repository.search.UserSitesSearchRepository;
import by.homesite.gator.service.UserSitesService;
import by.homesite.gator.service.dto.UserSitesDTO;
import by.homesite.gator.service.mapper.UserSitesMapper;
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
 * Integration tests for the {@link UserSitesResource} REST controller.
 */
@SpringBootTest(classes = GatorApp.class)
public class UserSitesResourceIT {

    @Autowired
    private UserSitesRepository userSitesRepository;

    @Autowired
    private UserSitesMapper userSitesMapper;

    @Autowired
    private UserSitesService userSitesService;

    /**
     * This repository is mocked in the by.homesite.gator.repository.search test package.
     *
     * @see by.homesite.gator.repository.search.UserSitesSearchRepositoryMockConfiguration
     */
    @Autowired
    private UserSitesSearchRepository mockUserSitesSearchRepository;

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

    private MockMvc restUserSitesMockMvc;

    private UserSites userSites;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final UserSitesResource userSitesResource = new UserSitesResource(userSitesService);
        this.restUserSitesMockMvc = MockMvcBuilders.standaloneSetup(userSitesResource)
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
    public static UserSites createEntity(EntityManager em) {
        UserSites userSites = new UserSites();
        return userSites;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static UserSites createUpdatedEntity(EntityManager em) {
        UserSites userSites = new UserSites();
        return userSites;
    }

    @BeforeEach
    public void initTest() {
        userSites = createEntity(em);
    }

    @Test
    @Transactional
    public void createUserSites() throws Exception {
        int databaseSizeBeforeCreate = userSitesRepository.findAll().size();

        // Create the UserSites
        UserSitesDTO userSitesDTO = userSitesMapper.toDto(userSites);
        restUserSitesMockMvc.perform(post("/api/user-sites")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(userSitesDTO)))
            .andExpect(status().isCreated());

        // Validate the UserSites in the database
        List<UserSites> userSitesList = userSitesRepository.findAll();
        assertThat(userSitesList).hasSize(databaseSizeBeforeCreate + 1);
        UserSites testUserSites = userSitesList.get(userSitesList.size() - 1);

        // Validate the UserSites in Elasticsearch
        verify(mockUserSitesSearchRepository, times(1)).save(testUserSites);
    }

    @Test
    @Transactional
    public void createUserSitesWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = userSitesRepository.findAll().size();

        // Create the UserSites with an existing ID
        userSites.setId(1L);
        UserSitesDTO userSitesDTO = userSitesMapper.toDto(userSites);

        // An entity with an existing ID cannot be created, so this API call must fail
        restUserSitesMockMvc.perform(post("/api/user-sites")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(userSitesDTO)))
            .andExpect(status().isBadRequest());

        // Validate the UserSites in the database
        List<UserSites> userSitesList = userSitesRepository.findAll();
        assertThat(userSitesList).hasSize(databaseSizeBeforeCreate);

        // Validate the UserSites in Elasticsearch
        verify(mockUserSitesSearchRepository, times(0)).save(userSites);
    }


    @Test
    @Transactional
    public void getAllUserSites() throws Exception {
        // Initialize the database
        userSitesRepository.saveAndFlush(userSites);

        // Get all the userSitesList
        restUserSitesMockMvc.perform(get("/api/user-sites?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(userSites.getId().intValue())));
    }
    
    @Test
    @Transactional
    public void getUserSites() throws Exception {
        // Initialize the database
        userSitesRepository.saveAndFlush(userSites);

        // Get the userSites
        restUserSitesMockMvc.perform(get("/api/user-sites/{id}", userSites.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(userSites.getId().intValue()));
    }

    @Test
    @Transactional
    public void getNonExistingUserSites() throws Exception {
        // Get the userSites
        restUserSitesMockMvc.perform(get("/api/user-sites/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateUserSites() throws Exception {
        // Initialize the database
        userSitesRepository.saveAndFlush(userSites);

        int databaseSizeBeforeUpdate = userSitesRepository.findAll().size();

        // Update the userSites
        UserSites updatedUserSites = userSitesRepository.findById(userSites.getId()).get();
        // Disconnect from session so that the updates on updatedUserSites are not directly saved in db
        em.detach(updatedUserSites);
        UserSitesDTO userSitesDTO = userSitesMapper.toDto(updatedUserSites);

        restUserSitesMockMvc.perform(put("/api/user-sites")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(userSitesDTO)))
            .andExpect(status().isOk());

        // Validate the UserSites in the database
        List<UserSites> userSitesList = userSitesRepository.findAll();
        assertThat(userSitesList).hasSize(databaseSizeBeforeUpdate);
        UserSites testUserSites = userSitesList.get(userSitesList.size() - 1);

        // Validate the UserSites in Elasticsearch
        verify(mockUserSitesSearchRepository, times(1)).save(testUserSites);
    }

    @Test
    @Transactional
    public void updateNonExistingUserSites() throws Exception {
        int databaseSizeBeforeUpdate = userSitesRepository.findAll().size();

        // Create the UserSites
        UserSitesDTO userSitesDTO = userSitesMapper.toDto(userSites);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restUserSitesMockMvc.perform(put("/api/user-sites")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(userSitesDTO)))
            .andExpect(status().isBadRequest());

        // Validate the UserSites in the database
        List<UserSites> userSitesList = userSitesRepository.findAll();
        assertThat(userSitesList).hasSize(databaseSizeBeforeUpdate);

        // Validate the UserSites in Elasticsearch
        verify(mockUserSitesSearchRepository, times(0)).save(userSites);
    }

    @Test
    @Transactional
    public void deleteUserSites() throws Exception {
        // Initialize the database
        userSitesRepository.saveAndFlush(userSites);

        int databaseSizeBeforeDelete = userSitesRepository.findAll().size();

        // Delete the userSites
        restUserSitesMockMvc.perform(delete("/api/user-sites/{id}", userSites.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<UserSites> userSitesList = userSitesRepository.findAll();
        assertThat(userSitesList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the UserSites in Elasticsearch
        verify(mockUserSitesSearchRepository, times(1)).deleteById(userSites.getId());
    }

    @Test
    @Transactional
    public void searchUserSites() throws Exception {
        // Initialize the database
        userSitesRepository.saveAndFlush(userSites);
        when(mockUserSitesSearchRepository.search(queryStringQuery("id:" + userSites.getId())))
            .thenReturn(Collections.singletonList(userSites));
        // Search the userSites
        restUserSitesMockMvc.perform(get("/api/_search/user-sites?query=id:" + userSites.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(userSites.getId().intValue())));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(UserSites.class);
        UserSites userSites1 = new UserSites();
        userSites1.setId(1L);
        UserSites userSites2 = new UserSites();
        userSites2.setId(userSites1.getId());
        assertThat(userSites1).isEqualTo(userSites2);
        userSites2.setId(2L);
        assertThat(userSites1).isNotEqualTo(userSites2);
        userSites1.setId(null);
        assertThat(userSites1).isNotEqualTo(userSites2);
    }

    @Test
    @Transactional
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(UserSitesDTO.class);
        UserSitesDTO userSitesDTO1 = new UserSitesDTO();
        userSitesDTO1.setId(1L);
        UserSitesDTO userSitesDTO2 = new UserSitesDTO();
        assertThat(userSitesDTO1).isNotEqualTo(userSitesDTO2);
        userSitesDTO2.setId(userSitesDTO1.getId());
        assertThat(userSitesDTO1).isEqualTo(userSitesDTO2);
        userSitesDTO2.setId(2L);
        assertThat(userSitesDTO1).isNotEqualTo(userSitesDTO2);
        userSitesDTO1.setId(null);
        assertThat(userSitesDTO1).isNotEqualTo(userSitesDTO2);
    }

    @Test
    @Transactional
    public void testEntityFromId() {
        assertThat(userSitesMapper.fromId(42L).getId()).isEqualTo(42);
        assertThat(userSitesMapper.fromId(null)).isNull();
    }
}
