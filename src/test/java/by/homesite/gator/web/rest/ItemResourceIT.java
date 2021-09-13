package by.homesite.gator.web.rest;

import static by.homesite.gator.web.rest.TestUtil.createFormattingConversionService;
import static by.homesite.gator.web.rest.TestUtil.sameInstant;
import static org.assertj.core.api.Assertions.assertThat;
import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import by.homesite.gator.GatorApp;
import by.homesite.gator.domain.Item;
import by.homesite.gator.repository.ItemRepository;
import by.homesite.gator.repository.search.ItemSearchRepository;
import by.homesite.gator.service.ItemService;
import by.homesite.gator.service.dto.ItemDTO;
import by.homesite.gator.service.mapper.ItemMapper;
import by.homesite.gator.web.rest.errors.ExceptionTranslator;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Validator;

/**
 * Integration tests for the {@link ItemResource} REST controller.
 */
@SpringBootTest(classes = GatorApp.class)
public class ItemResourceIT {

    private static final String DEFAULT_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_TITLE = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final Float DEFAULT_PRICE = 1F;
    private static final Float UPDATED_PRICE = 2F;
    private static final Float SMALLER_PRICE = 1F - 1F;

    private static final String DEFAULT_LINK = "AAAAAAAAAA";
    private static final String UPDATED_LINK = "BBBBBBBBBB";

    private static final String DEFAULT_OWNER_NAME = "AAAAAAAAAA";
    private static final String UPDATED_OWNER_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_OWNER_LINK = "AAAAAAAAAA";
    private static final String UPDATED_OWNER_LINK = "BBBBBBBBBB";

    private static final String DEFAULT_IMAGE = "AAAAAAAAAA";
    private static final String UPDATED_IMAGE = "BBBBBBBBBB";

    private static final Boolean DEFAULT_ACTIVE = false;
    private static final Boolean UPDATED_ACTIVE = true;

    private static final ZonedDateTime DEFAULT_CREATED_AT = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_CREATED_AT = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final ZonedDateTime SMALLER_CREATED_AT = ZonedDateTime.ofInstant(Instant.ofEpochMilli(-1L), ZoneOffset.UTC);

    private static final ZonedDateTime DEFAULT_UPDATED_AT = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_UPDATED_AT = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final ZonedDateTime SMALLER_UPDATED_AT = ZonedDateTime.ofInstant(Instant.ofEpochMilli(-1L), ZoneOffset.UTC);

    private static final ZonedDateTime DEFAULT_DELETED_AT = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_DELETED_AT = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final ZonedDateTime SMALLER_DELETED_AT = ZonedDateTime.ofInstant(Instant.ofEpochMilli(-1L), ZoneOffset.UTC);

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private ItemMapper itemMapper;

    @Autowired
    private ItemService itemService;

    /**
     * This repository is mocked in the by.homesite.gator.repository.search test package.
     *
     * @see by.homesite.gator.repository.search.ItemSearchRepositoryMockConfiguration
     */
    @Autowired
    private ItemSearchRepository mockItemSearchRepository;

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

    private MockMvc restItemMockMvc;

    private Item item;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final ItemResource itemResource = new ItemResource(itemService, itemRepository);
        this.restItemMockMvc =
            MockMvcBuilders
                .standaloneSetup(itemResource)
                .setCustomArgumentResolvers(pageableArgumentResolver)
                .setControllerAdvice(exceptionTranslator)
                .setConversionService(createFormattingConversionService())
                .setMessageConverters(jacksonMessageConverter)
                .setValidator(validator)
                .build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Item createEntity(EntityManager em) {
        Item item = new Item()
            .title(DEFAULT_TITLE)
            .description(DEFAULT_DESCRIPTION)
            .price(DEFAULT_PRICE)
            .link(DEFAULT_LINK)
            .ownerName(DEFAULT_OWNER_NAME)
            .ownerLink(DEFAULT_OWNER_LINK)
            .image(DEFAULT_IMAGE)
            .active(DEFAULT_ACTIVE)
            .createdAt(DEFAULT_CREATED_AT)
            .updatedAt(DEFAULT_UPDATED_AT)
            .deletedAt(DEFAULT_DELETED_AT);
        return item;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Item createUpdatedEntity(EntityManager em) {
        Item item = new Item()
            .title(UPDATED_TITLE)
            .description(UPDATED_DESCRIPTION)
            .price(UPDATED_PRICE)
            .link(UPDATED_LINK)
            .ownerName(UPDATED_OWNER_NAME)
            .ownerLink(UPDATED_OWNER_LINK)
            .image(UPDATED_IMAGE)
            .active(UPDATED_ACTIVE)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT)
            .deletedAt(UPDATED_DELETED_AT);
        return item;
    }

    @BeforeEach
    public void initTest() {
        item = createEntity(em);
    }

    /*@Test
    @Transactional
    public void createItem() throws Exception {
        int databaseSizeBeforeCreate = itemRepository.findAll().size();

        // Create the Item
        ItemDTO itemDTO = itemMapper.toDto(item);
        restItemMockMvc.perform(post("/api/items")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(itemDTO)))
            .andExpect(status().isCreated());

        // Validate the Item in the database
        List<Item> itemList = itemRepository.findAll();
        assertThat(itemList).hasSize(databaseSizeBeforeCreate + 1);
        Item testItem = itemList.get(itemList.size() - 1);
        assertThat(testItem.getTitle()).isEqualTo(DEFAULT_TITLE);
        assertThat(testItem.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testItem.getPrice()).isEqualTo(DEFAULT_PRICE);
        assertThat(testItem.getLink()).isEqualTo(DEFAULT_LINK);
        assertThat(testItem.getOwnerName()).isEqualTo(DEFAULT_OWNER_NAME);
        assertThat(testItem.getOwnerLink()).isEqualTo(DEFAULT_OWNER_LINK);
        assertThat(testItem.getImage()).isEqualTo(DEFAULT_IMAGE);
        assertThat(testItem.isActive()).isEqualTo(DEFAULT_ACTIVE);
        assertThat(testItem.getCreatedAt()).isEqualTo(DEFAULT_CREATED_AT);
        assertThat(testItem.getUpdatedAt()).isEqualTo(DEFAULT_UPDATED_AT);
        assertThat(testItem.getDeletedAt()).isEqualTo(DEFAULT_DELETED_AT);

        // Validate the Item in Elasticsearch
        verify(mockItemSearchRepository, times(1)).save(testItem);
    }*/

    @Test
    @Transactional
    public void createItemWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = itemRepository.findAll().size();

        // Create the Item with an existing ID
        item.setId(1L);
        ItemDTO itemDTO = itemMapper.toDto(item);

        // An entity with an existing ID cannot be created, so this API call must fail
        restItemMockMvc.perform(post("/api/items").content(TestUtil.convertObjectToJsonBytes(itemDTO))).andExpect(status().isBadRequest());

        // Validate the Item in the database
        List<Item> itemList = itemRepository.findAll();
        assertThat(itemList).hasSize(databaseSizeBeforeCreate);

        // Validate the Item in Elasticsearch
        verify(mockItemSearchRepository, times(0)).save(item);
    }

    @Test
    @Transactional
    public void checkTitleIsRequired() throws Exception {
        int databaseSizeBeforeTest = itemRepository.findAll().size();
        // set the field null
        item.setTitle(null);

        // Create the Item, which fails.
        ItemDTO itemDTO = itemMapper.toDto(item);

        restItemMockMvc.perform(post("/api/items").content(TestUtil.convertObjectToJsonBytes(itemDTO))).andExpect(status().isBadRequest());

        List<Item> itemList = itemRepository.findAll();
        assertThat(itemList).hasSize(databaseSizeBeforeTest);
    }

    /* @Test
    @Transactional
    public void getAllItems() throws Exception {
        // Initialize the database
        itemRepository.saveAndFlush(item);

        // Get all the itemList
        restItemMockMvc.perform(get("/api/items?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(item.getId().intValue())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE.toString())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())))
            .andExpect(jsonPath("$.[*].price").value(hasItem(DEFAULT_PRICE.doubleValue())))
            .andExpect(jsonPath("$.[*].link").value(hasItem(DEFAULT_LINK.toString())))
            .andExpect(jsonPath("$.[*].ownerName").value(hasItem(DEFAULT_OWNER_NAME.toString())))
            .andExpect(jsonPath("$.[*].ownerLink").value(hasItem(DEFAULT_OWNER_LINK.toString())))
            .andExpect(jsonPath("$.[*].image").value(hasItem(DEFAULT_IMAGE.toString())))
            .andExpect(jsonPath("$.[*].active").value(hasItem(DEFAULT_ACTIVE.booleanValue())))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(sameInstant(DEFAULT_CREATED_AT))))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(sameInstant(DEFAULT_UPDATED_AT))))
            .andExpect(jsonPath("$.[*].deletedAt").value(hasItem(sameInstant(DEFAULT_DELETED_AT))));
    }

    @Test
    @Transactional
    public void getItem() throws Exception {
        // Initialize the database
        itemRepository.saveAndFlush(item);

        // Get the item
        restItemMockMvc.perform(get("/api/items/{id}", item.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(item.getId().intValue()))
            .andExpect(jsonPath("$.title").value(DEFAULT_TITLE.toString()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION.toString()))
            .andExpect(jsonPath("$.price").value(DEFAULT_PRICE.doubleValue()))
            .andExpect(jsonPath("$.link").value(DEFAULT_LINK.toString()))
            .andExpect(jsonPath("$.ownerName").value(DEFAULT_OWNER_NAME.toString()))
            .andExpect(jsonPath("$.ownerLink").value(DEFAULT_OWNER_LINK.toString()))
            .andExpect(jsonPath("$.image").value(DEFAULT_IMAGE.toString()))
            .andExpect(jsonPath("$.active").value(DEFAULT_ACTIVE.booleanValue()))
            .andExpect(jsonPath("$.createdAt").value(sameInstant(DEFAULT_CREATED_AT)))
            .andExpect(jsonPath("$.updatedAt").value(sameInstant(DEFAULT_UPDATED_AT)))
            .andExpect(jsonPath("$.deletedAt").value(sameInstant(DEFAULT_DELETED_AT)));
    }*/

    @Test
    @Transactional
    public void getNonExistingItem() throws Exception {
        // Get the item
        restItemMockMvc.perform(get("/api/items/{id}", Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    /*@Test
    @Transactional
    public void updateItem() throws Exception {
        // Initialize the database
        itemRepository.saveAndFlush(item);

        int databaseSizeBeforeUpdate = itemRepository.findAll().size();

        // Update the item
        Item updatedItem = itemRepository.findById(item.getId()).get();
        // Disconnect from session so that the updates on updatedItem are not directly saved in db
        em.detach(updatedItem);
        updatedItem
            .title(UPDATED_TITLE)
            .description(UPDATED_DESCRIPTION)
            .price(UPDATED_PRICE)
            .link(UPDATED_LINK)
            .ownerName(UPDATED_OWNER_NAME)
            .ownerLink(UPDATED_OWNER_LINK)
            .image(UPDATED_IMAGE)
            .active(UPDATED_ACTIVE)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT)
            .deletedAt(UPDATED_DELETED_AT);
        ItemDTO itemDTO = itemMapper.toDto(updatedItem);

        restItemMockMvc.perform(put("/api/items")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(itemDTO)))
            .andExpect(status().isOk());

        // Validate the Item in the database
        List<Item> itemList = itemRepository.findAll();
        assertThat(itemList).hasSize(databaseSizeBeforeUpdate);
        Item testItem = itemList.get(itemList.size() - 1);
        assertThat(testItem.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testItem.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testItem.getPrice()).isEqualTo(UPDATED_PRICE);
        assertThat(testItem.getLink()).isEqualTo(UPDATED_LINK);
        assertThat(testItem.getOwnerName()).isEqualTo(UPDATED_OWNER_NAME);
        assertThat(testItem.getOwnerLink()).isEqualTo(UPDATED_OWNER_LINK);
        assertThat(testItem.getImage()).isEqualTo(UPDATED_IMAGE);
        assertThat(testItem.isActive()).isEqualTo(UPDATED_ACTIVE);
        assertThat(testItem.getCreatedAt()).isEqualTo(UPDATED_CREATED_AT);
        assertThat(testItem.getUpdatedAt()).isEqualTo(UPDATED_UPDATED_AT);
        assertThat(testItem.getDeletedAt()).isEqualTo(UPDATED_DELETED_AT);

        // Validate the Item in Elasticsearch
        verify(mockItemSearchRepository, times(1)).save(testItem);
    }*/

    @Test
    @Transactional
    public void updateNonExistingItem() throws Exception {
        int databaseSizeBeforeUpdate = itemRepository.findAll().size();

        // Create the Item
        ItemDTO itemDTO = itemMapper.toDto(item);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restItemMockMvc.perform(put("/api/items").content(TestUtil.convertObjectToJsonBytes(itemDTO))).andExpect(status().isBadRequest());

        // Validate the Item in the database
        List<Item> itemList = itemRepository.findAll();
        assertThat(itemList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Item in Elasticsearch
        verify(mockItemSearchRepository, times(0)).save(item);
    }

    /*@Test
    @Transactional
    public void deleteItem() throws Exception {
        // Initialize the database
        itemRepository.saveAndFlush(item);

        int databaseSizeBeforeDelete = itemRepository.findAll().size();

        // Delete the item
        restItemMockMvc.perform(delete("/api/items/{id}", item.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Item> itemList = itemRepository.findAll();
        assertThat(itemList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the Item in Elasticsearch
        verify(mockItemSearchRepository, times(1)).deleteById(item.getId());
    }*/

    /*@Test
    @Transactional
    public void searchItem() throws Exception {
        // Initialize the database
        itemRepository.saveAndFlush(item);
        when(mockItemSearchRepository.search(queryStringQuery("id:" + item.getId()), PageRequest.of(0, 20)))
            .thenReturn(new PageImpl<>(Collections.singletonList(item), PageRequest.of(0, 1), 1));
        // Search the item
        restItemMockMvc.perform(get("/api/_search/items?query=id:" + item.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(item.getId().intValue())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].price").value(hasItem(DEFAULT_PRICE.doubleValue())))
            .andExpect(jsonPath("$.[*].link").value(hasItem(DEFAULT_LINK)))
            .andExpect(jsonPath("$.[*].ownerName").value(hasItem(DEFAULT_OWNER_NAME)))
            .andExpect(jsonPath("$.[*].ownerLink").value(hasItem(DEFAULT_OWNER_LINK)))
            .andExpect(jsonPath("$.[*].image").value(hasItem(DEFAULT_IMAGE)))
            .andExpect(jsonPath("$.[*].active").value(hasItem(DEFAULT_ACTIVE.booleanValue())))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(sameInstant(DEFAULT_CREATED_AT))))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(sameInstant(DEFAULT_UPDATED_AT))))
            .andExpect(jsonPath("$.[*].deletedAt").value(hasItem(sameInstant(DEFAULT_DELETED_AT))));
    }*/

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Item.class);
        Item item1 = new Item();
        item1.setId(1L);
        Item item2 = new Item();
        item2.setId(item1.getId());
        assertThat(item1).isEqualTo(item2);
        item2.setId(2L);
        assertThat(item1).isNotEqualTo(item2);
        item1.setId(null);
        assertThat(item1).isNotEqualTo(item2);
    }

    @Test
    @Transactional
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ItemDTO.class);
        ItemDTO itemDTO1 = new ItemDTO();
        itemDTO1.setId(1L);
        ItemDTO itemDTO2 = new ItemDTO();
        assertThat(itemDTO1).isNotEqualTo(itemDTO2);
        itemDTO2.setId(itemDTO1.getId());
        assertThat(itemDTO1).isEqualTo(itemDTO2);
        itemDTO2.setId(2L);
        assertThat(itemDTO1).isNotEqualTo(itemDTO2);
        itemDTO1.setId(null);
        assertThat(itemDTO1).isNotEqualTo(itemDTO2);
    }
}
