package by.homesite.gator.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import by.homesite.gator.IntegrationTest;
import by.homesite.gator.domain.UserProperties;
import by.homesite.gator.repository.UserPropertiesRepository;
import by.homesite.gator.repository.search.UserPropertiesSearchRepository;
import by.homesite.gator.service.dto.UserPropertiesDTO;
import by.homesite.gator.service.mapper.UserPropertiesMapper;
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
 * Integration tests for the {@link UserPropertiesResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class UserPropertiesResourceIT {

    private static final String DEFAULT_VALUE = "AAAAAAAAAA";
    private static final String UPDATED_VALUE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/user-properties";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/user-properties";

    private static final Random random = new Random();
    private static final AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private UserPropertiesRepository userPropertiesRepository;

    @Autowired
    private UserPropertiesMapper userPropertiesMapper;

    /**
     * This repository is mocked in the by.homesite.gator.repository.search test package.
     *
     * @see by.homesite.gator.repository.search.UserPropertiesSearchRepositoryMockConfiguration
     */
    @Autowired
    private UserPropertiesSearchRepository mockUserPropertiesSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restUserPropertiesMockMvc;

    private UserProperties userProperties;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static UserProperties createEntity(EntityManager em) {
        UserProperties userProperties = new UserProperties().value(DEFAULT_VALUE);
        return userProperties;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static UserProperties createUpdatedEntity(EntityManager em) {
        UserProperties userProperties = new UserProperties().value(UPDATED_VALUE);
        return userProperties;
    }

    @BeforeEach
    public void initTest() {
        userProperties = createEntity(em);
    }

    @Test
    @Transactional
    void createUserProperties() throws Exception {
        int databaseSizeBeforeCreate = userPropertiesRepository.findAll().size();
        // Create the UserProperties
        UserPropertiesDTO userPropertiesDTO = userPropertiesMapper.toDto(userProperties);
        restUserPropertiesMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(userPropertiesDTO))
            )
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
    void createUserPropertiesWithExistingId() throws Exception {
        // Create the UserProperties with an existing ID
        userProperties.setId(1L);
        UserPropertiesDTO userPropertiesDTO = userPropertiesMapper.toDto(userProperties);

        int databaseSizeBeforeCreate = userPropertiesRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restUserPropertiesMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(userPropertiesDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the UserProperties in the database
        List<UserProperties> userPropertiesList = userPropertiesRepository.findAll();
        assertThat(userPropertiesList).hasSize(databaseSizeBeforeCreate);

        // Validate the UserProperties in Elasticsearch
        verify(mockUserPropertiesSearchRepository, times(0)).save(userProperties);
    }

    @Test
    @Transactional
    void getAllUserProperties() throws Exception {
        // Initialize the database
        userPropertiesRepository.saveAndFlush(userProperties);

        // Get all the userPropertiesList
        restUserPropertiesMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(userProperties.getId().intValue())))
            .andExpect(jsonPath("$.[*].value").value(hasItem(DEFAULT_VALUE)));
    }

    @Test
    @Transactional
    void getUserProperties() throws Exception {
        // Initialize the database
        userPropertiesRepository.saveAndFlush(userProperties);

        // Get the userProperties
        restUserPropertiesMockMvc
            .perform(get(ENTITY_API_URL_ID, userProperties.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(userProperties.getId().intValue()))
            .andExpect(jsonPath("$.value").value(DEFAULT_VALUE));
    }

    @Test
    @Transactional
    void getNonExistingUserProperties() throws Exception {
        // Get the userProperties
        restUserPropertiesMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewUserProperties() throws Exception {
        // Initialize the database
        userPropertiesRepository.saveAndFlush(userProperties);

        int databaseSizeBeforeUpdate = userPropertiesRepository.findAll().size();

        // Update the userProperties
        UserProperties updatedUserProperties = userPropertiesRepository.findById(userProperties.getId()).get();
        // Disconnect from session so that the updates on updatedUserProperties are not directly saved in db
        em.detach(updatedUserProperties);
        updatedUserProperties.value(UPDATED_VALUE);
        UserPropertiesDTO userPropertiesDTO = userPropertiesMapper.toDto(updatedUserProperties);

        restUserPropertiesMockMvc
            .perform(
                put(ENTITY_API_URL_ID, userPropertiesDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(userPropertiesDTO))
            )
            .andExpect(status().isOk());

        // Validate the UserProperties in the database
        List<UserProperties> userPropertiesList = userPropertiesRepository.findAll();
        assertThat(userPropertiesList).hasSize(databaseSizeBeforeUpdate);
        UserProperties testUserProperties = userPropertiesList.get(userPropertiesList.size() - 1);
        assertThat(testUserProperties.getValue()).isEqualTo(UPDATED_VALUE);

        // Validate the UserProperties in Elasticsearch
        verify(mockUserPropertiesSearchRepository).save(testUserProperties);
    }

    @Test
    @Transactional
    void putNonExistingUserProperties() throws Exception {
        int databaseSizeBeforeUpdate = userPropertiesRepository.findAll().size();
        userProperties.setId(count.incrementAndGet());

        // Create the UserProperties
        UserPropertiesDTO userPropertiesDTO = userPropertiesMapper.toDto(userProperties);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restUserPropertiesMockMvc
            .perform(
                put(ENTITY_API_URL_ID, userPropertiesDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(userPropertiesDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the UserProperties in the database
        List<UserProperties> userPropertiesList = userPropertiesRepository.findAll();
        assertThat(userPropertiesList).hasSize(databaseSizeBeforeUpdate);

        // Validate the UserProperties in Elasticsearch
        verify(mockUserPropertiesSearchRepository, times(0)).save(userProperties);
    }

    @Test
    @Transactional
    void putWithIdMismatchUserProperties() throws Exception {
        int databaseSizeBeforeUpdate = userPropertiesRepository.findAll().size();
        userProperties.setId(count.incrementAndGet());

        // Create the UserProperties
        UserPropertiesDTO userPropertiesDTO = userPropertiesMapper.toDto(userProperties);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUserPropertiesMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(userPropertiesDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the UserProperties in the database
        List<UserProperties> userPropertiesList = userPropertiesRepository.findAll();
        assertThat(userPropertiesList).hasSize(databaseSizeBeforeUpdate);

        // Validate the UserProperties in Elasticsearch
        verify(mockUserPropertiesSearchRepository, times(0)).save(userProperties);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamUserProperties() throws Exception {
        int databaseSizeBeforeUpdate = userPropertiesRepository.findAll().size();
        userProperties.setId(count.incrementAndGet());

        // Create the UserProperties
        UserPropertiesDTO userPropertiesDTO = userPropertiesMapper.toDto(userProperties);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUserPropertiesMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(userPropertiesDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the UserProperties in the database
        List<UserProperties> userPropertiesList = userPropertiesRepository.findAll();
        assertThat(userPropertiesList).hasSize(databaseSizeBeforeUpdate);

        // Validate the UserProperties in Elasticsearch
        verify(mockUserPropertiesSearchRepository, times(0)).save(userProperties);
    }

    @Test
    @Transactional
    void partialUpdateUserPropertiesWithPatch() throws Exception {
        // Initialize the database
        userPropertiesRepository.saveAndFlush(userProperties);

        int databaseSizeBeforeUpdate = userPropertiesRepository.findAll().size();

        // Update the userProperties using partial update
        UserProperties partialUpdatedUserProperties = new UserProperties();
        partialUpdatedUserProperties.setId(userProperties.getId());

        partialUpdatedUserProperties.value(UPDATED_VALUE);

        restUserPropertiesMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedUserProperties.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedUserProperties))
            )
            .andExpect(status().isOk());

        // Validate the UserProperties in the database
        List<UserProperties> userPropertiesList = userPropertiesRepository.findAll();
        assertThat(userPropertiesList).hasSize(databaseSizeBeforeUpdate);
        UserProperties testUserProperties = userPropertiesList.get(userPropertiesList.size() - 1);
        assertThat(testUserProperties.getValue()).isEqualTo(UPDATED_VALUE);
    }

    @Test
    @Transactional
    void fullUpdateUserPropertiesWithPatch() throws Exception {
        // Initialize the database
        userPropertiesRepository.saveAndFlush(userProperties);

        int databaseSizeBeforeUpdate = userPropertiesRepository.findAll().size();

        // Update the userProperties using partial update
        UserProperties partialUpdatedUserProperties = new UserProperties();
        partialUpdatedUserProperties.setId(userProperties.getId());

        partialUpdatedUserProperties.value(UPDATED_VALUE);

        restUserPropertiesMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedUserProperties.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedUserProperties))
            )
            .andExpect(status().isOk());

        // Validate the UserProperties in the database
        List<UserProperties> userPropertiesList = userPropertiesRepository.findAll();
        assertThat(userPropertiesList).hasSize(databaseSizeBeforeUpdate);
        UserProperties testUserProperties = userPropertiesList.get(userPropertiesList.size() - 1);
        assertThat(testUserProperties.getValue()).isEqualTo(UPDATED_VALUE);
    }

    @Test
    @Transactional
    void patchNonExistingUserProperties() throws Exception {
        int databaseSizeBeforeUpdate = userPropertiesRepository.findAll().size();
        userProperties.setId(count.incrementAndGet());

        // Create the UserProperties
        UserPropertiesDTO userPropertiesDTO = userPropertiesMapper.toDto(userProperties);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restUserPropertiesMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, userPropertiesDTO.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(userPropertiesDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the UserProperties in the database
        List<UserProperties> userPropertiesList = userPropertiesRepository.findAll();
        assertThat(userPropertiesList).hasSize(databaseSizeBeforeUpdate);

        // Validate the UserProperties in Elasticsearch
        verify(mockUserPropertiesSearchRepository, times(0)).save(userProperties);
    }

    @Test
    @Transactional
    void patchWithIdMismatchUserProperties() throws Exception {
        int databaseSizeBeforeUpdate = userPropertiesRepository.findAll().size();
        userProperties.setId(count.incrementAndGet());

        // Create the UserProperties
        UserPropertiesDTO userPropertiesDTO = userPropertiesMapper.toDto(userProperties);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUserPropertiesMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(userPropertiesDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the UserProperties in the database
        List<UserProperties> userPropertiesList = userPropertiesRepository.findAll();
        assertThat(userPropertiesList).hasSize(databaseSizeBeforeUpdate);

        // Validate the UserProperties in Elasticsearch
        verify(mockUserPropertiesSearchRepository, times(0)).save(userProperties);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamUserProperties() throws Exception {
        int databaseSizeBeforeUpdate = userPropertiesRepository.findAll().size();
        userProperties.setId(count.incrementAndGet());

        // Create the UserProperties
        UserPropertiesDTO userPropertiesDTO = userPropertiesMapper.toDto(userProperties);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUserPropertiesMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(userPropertiesDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the UserProperties in the database
        List<UserProperties> userPropertiesList = userPropertiesRepository.findAll();
        assertThat(userPropertiesList).hasSize(databaseSizeBeforeUpdate);

        // Validate the UserProperties in Elasticsearch
        verify(mockUserPropertiesSearchRepository, times(0)).save(userProperties);
    }

    @Test
    @Transactional
    void deleteUserProperties() throws Exception {
        // Initialize the database
        userPropertiesRepository.saveAndFlush(userProperties);

        int databaseSizeBeforeDelete = userPropertiesRepository.findAll().size();

        // Delete the userProperties
        restUserPropertiesMockMvc
            .perform(delete(ENTITY_API_URL_ID, userProperties.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<UserProperties> userPropertiesList = userPropertiesRepository.findAll();
        assertThat(userPropertiesList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the UserProperties in Elasticsearch
        verify(mockUserPropertiesSearchRepository, times(1)).deleteById(userProperties.getId());
    }

    @Test
    @Transactional
    void searchUserProperties() throws Exception {
        // Configure the mock search repository
        // Initialize the database
        userPropertiesRepository.saveAndFlush(userProperties);
        when(mockUserPropertiesSearchRepository.search(queryStringQuery("id:" + userProperties.getId())))
            .thenReturn(Collections.singletonList(userProperties));

        // Search the userProperties
        restUserPropertiesMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + userProperties.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(userProperties.getId().intValue())))
            .andExpect(jsonPath("$.[*].value").value(hasItem(DEFAULT_VALUE)));
    }
}
