package by.homesite.gator.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import by.homesite.gator.IntegrationTest;
import by.homesite.gator.domain.UserSites;
import by.homesite.gator.repository.UserSitesRepository;
import by.homesite.gator.repository.search.UserSitesSearchRepository;
import by.homesite.gator.service.dto.UserSitesDTO;
import by.homesite.gator.service.mapper.UserSitesMapper;
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
 * Integration tests for the {@link UserSitesResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class UserSitesResourceIT {

    private static final String ENTITY_API_URL = "/api/user-sites";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/user-sites";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private UserSitesRepository userSitesRepository;

    @Autowired
    private UserSitesMapper userSitesMapper;

    /**
     * This repository is mocked in the by.homesite.gator.repository.search test package.
     *
     * @see by.homesite.gator.repository.search.UserSitesSearchRepositoryMockConfiguration
     */
    @Autowired
    private UserSitesSearchRepository mockUserSitesSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restUserSitesMockMvc;

    private UserSites userSites;

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
    void createUserSites() throws Exception {
        int databaseSizeBeforeCreate = userSitesRepository.findAll().size();
        // Create the UserSites
        UserSitesDTO userSitesDTO = userSitesMapper.toDto(userSites);
        restUserSitesMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(userSitesDTO))
            )
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
    void createUserSitesWithExistingId() throws Exception {
        // Create the UserSites with an existing ID
        userSites.setId(1L);
        UserSitesDTO userSitesDTO = userSitesMapper.toDto(userSites);

        int databaseSizeBeforeCreate = userSitesRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restUserSitesMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(userSitesDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the UserSites in the database
        List<UserSites> userSitesList = userSitesRepository.findAll();
        assertThat(userSitesList).hasSize(databaseSizeBeforeCreate);

        // Validate the UserSites in Elasticsearch
        verify(mockUserSitesSearchRepository, times(0)).save(userSites);
    }

    @Test
    @Transactional
    void getAllUserSites() throws Exception {
        // Initialize the database
        userSitesRepository.saveAndFlush(userSites);

        // Get all the userSitesList
        restUserSitesMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(userSites.getId().intValue())));
    }

    @Test
    @Transactional
    void getUserSites() throws Exception {
        // Initialize the database
        userSitesRepository.saveAndFlush(userSites);

        // Get the userSites
        restUserSitesMockMvc
            .perform(get(ENTITY_API_URL_ID, userSites.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(userSites.getId().intValue()));
    }

    @Test
    @Transactional
    void getNonExistingUserSites() throws Exception {
        // Get the userSites
        restUserSitesMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewUserSites() throws Exception {
        // Initialize the database
        userSitesRepository.saveAndFlush(userSites);

        int databaseSizeBeforeUpdate = userSitesRepository.findAll().size();

        // Update the userSites
        UserSites updatedUserSites = userSitesRepository.findById(userSites.getId()).get();
        // Disconnect from session so that the updates on updatedUserSites are not directly saved in db
        em.detach(updatedUserSites);
        UserSitesDTO userSitesDTO = userSitesMapper.toDto(updatedUserSites);

        restUserSitesMockMvc
            .perform(
                put(ENTITY_API_URL_ID, userSitesDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(userSitesDTO))
            )
            .andExpect(status().isOk());

        // Validate the UserSites in the database
        List<UserSites> userSitesList = userSitesRepository.findAll();
        assertThat(userSitesList).hasSize(databaseSizeBeforeUpdate);
        UserSites testUserSites = userSitesList.get(userSitesList.size() - 1);

        // Validate the UserSites in Elasticsearch
        verify(mockUserSitesSearchRepository).save(testUserSites);
    }

    @Test
    @Transactional
    void putNonExistingUserSites() throws Exception {
        int databaseSizeBeforeUpdate = userSitesRepository.findAll().size();
        userSites.setId(count.incrementAndGet());

        // Create the UserSites
        UserSitesDTO userSitesDTO = userSitesMapper.toDto(userSites);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restUserSitesMockMvc
            .perform(
                put(ENTITY_API_URL_ID, userSitesDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(userSitesDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the UserSites in the database
        List<UserSites> userSitesList = userSitesRepository.findAll();
        assertThat(userSitesList).hasSize(databaseSizeBeforeUpdate);

        // Validate the UserSites in Elasticsearch
        verify(mockUserSitesSearchRepository, times(0)).save(userSites);
    }

    @Test
    @Transactional
    void putWithIdMismatchUserSites() throws Exception {
        int databaseSizeBeforeUpdate = userSitesRepository.findAll().size();
        userSites.setId(count.incrementAndGet());

        // Create the UserSites
        UserSitesDTO userSitesDTO = userSitesMapper.toDto(userSites);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUserSitesMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(userSitesDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the UserSites in the database
        List<UserSites> userSitesList = userSitesRepository.findAll();
        assertThat(userSitesList).hasSize(databaseSizeBeforeUpdate);

        // Validate the UserSites in Elasticsearch
        verify(mockUserSitesSearchRepository, times(0)).save(userSites);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamUserSites() throws Exception {
        int databaseSizeBeforeUpdate = userSitesRepository.findAll().size();
        userSites.setId(count.incrementAndGet());

        // Create the UserSites
        UserSitesDTO userSitesDTO = userSitesMapper.toDto(userSites);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUserSitesMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(userSitesDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the UserSites in the database
        List<UserSites> userSitesList = userSitesRepository.findAll();
        assertThat(userSitesList).hasSize(databaseSizeBeforeUpdate);

        // Validate the UserSites in Elasticsearch
        verify(mockUserSitesSearchRepository, times(0)).save(userSites);
    }

    @Test
    @Transactional
    void partialUpdateUserSitesWithPatch() throws Exception {
        // Initialize the database
        userSitesRepository.saveAndFlush(userSites);

        int databaseSizeBeforeUpdate = userSitesRepository.findAll().size();

        // Update the userSites using partial update
        UserSites partialUpdatedUserSites = new UserSites();
        partialUpdatedUserSites.setId(userSites.getId());

        restUserSitesMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedUserSites.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedUserSites))
            )
            .andExpect(status().isOk());

        // Validate the UserSites in the database
        List<UserSites> userSitesList = userSitesRepository.findAll();
        assertThat(userSitesList).hasSize(databaseSizeBeforeUpdate);
        UserSites testUserSites = userSitesList.get(userSitesList.size() - 1);
    }

    @Test
    @Transactional
    void fullUpdateUserSitesWithPatch() throws Exception {
        // Initialize the database
        userSitesRepository.saveAndFlush(userSites);

        int databaseSizeBeforeUpdate = userSitesRepository.findAll().size();

        // Update the userSites using partial update
        UserSites partialUpdatedUserSites = new UserSites();
        partialUpdatedUserSites.setId(userSites.getId());

        restUserSitesMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedUserSites.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedUserSites))
            )
            .andExpect(status().isOk());

        // Validate the UserSites in the database
        List<UserSites> userSitesList = userSitesRepository.findAll();
        assertThat(userSitesList).hasSize(databaseSizeBeforeUpdate);
        UserSites testUserSites = userSitesList.get(userSitesList.size() - 1);
    }

    @Test
    @Transactional
    void patchNonExistingUserSites() throws Exception {
        int databaseSizeBeforeUpdate = userSitesRepository.findAll().size();
        userSites.setId(count.incrementAndGet());

        // Create the UserSites
        UserSitesDTO userSitesDTO = userSitesMapper.toDto(userSites);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restUserSitesMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, userSitesDTO.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(userSitesDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the UserSites in the database
        List<UserSites> userSitesList = userSitesRepository.findAll();
        assertThat(userSitesList).hasSize(databaseSizeBeforeUpdate);

        // Validate the UserSites in Elasticsearch
        verify(mockUserSitesSearchRepository, times(0)).save(userSites);
    }

    @Test
    @Transactional
    void patchWithIdMismatchUserSites() throws Exception {
        int databaseSizeBeforeUpdate = userSitesRepository.findAll().size();
        userSites.setId(count.incrementAndGet());

        // Create the UserSites
        UserSitesDTO userSitesDTO = userSitesMapper.toDto(userSites);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUserSitesMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(userSitesDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the UserSites in the database
        List<UserSites> userSitesList = userSitesRepository.findAll();
        assertThat(userSitesList).hasSize(databaseSizeBeforeUpdate);

        // Validate the UserSites in Elasticsearch
        verify(mockUserSitesSearchRepository, times(0)).save(userSites);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamUserSites() throws Exception {
        int databaseSizeBeforeUpdate = userSitesRepository.findAll().size();
        userSites.setId(count.incrementAndGet());

        // Create the UserSites
        UserSitesDTO userSitesDTO = userSitesMapper.toDto(userSites);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUserSitesMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(userSitesDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the UserSites in the database
        List<UserSites> userSitesList = userSitesRepository.findAll();
        assertThat(userSitesList).hasSize(databaseSizeBeforeUpdate);

        // Validate the UserSites in Elasticsearch
        verify(mockUserSitesSearchRepository, times(0)).save(userSites);
    }

    @Test
    @Transactional
    void deleteUserSites() throws Exception {
        // Initialize the database
        userSitesRepository.saveAndFlush(userSites);

        int databaseSizeBeforeDelete = userSitesRepository.findAll().size();

        // Delete the userSites
        restUserSitesMockMvc
            .perform(delete(ENTITY_API_URL_ID, userSites.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<UserSites> userSitesList = userSitesRepository.findAll();
        assertThat(userSitesList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the UserSites in Elasticsearch
        verify(mockUserSitesSearchRepository, times(1)).deleteById(userSites.getId());
    }

    @Test
    @Transactional
    void searchUserSites() throws Exception {
        // Configure the mock search repository
        // Initialize the database
        userSitesRepository.saveAndFlush(userSites);
        when(mockUserSitesSearchRepository.search(queryStringQuery("id:" + userSites.getId())))
            .thenReturn(Collections.singletonList(userSites));

        // Search the userSites
        restUserSitesMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + userSites.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(userSites.getId().intValue())));
    }
}
