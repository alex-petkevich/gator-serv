package by.homesite.gator.service;

import com.fasterxml.jackson.annotation.JsonIgnore;

import by.homesite.gator.domain.*;
import by.homesite.gator.repository.*;
import by.homesite.gator.repository.search.*;
import io.micrometer.core.annotation.Timed;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.ManyToMany;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class ElasticsearchIndexService {

    private static final Lock reindexLock = new ReentrantLock();

    private final Logger log = LoggerFactory.getLogger(ElasticsearchIndexService.class);

    private final CategoryRepository categoryRepository;

    private final CategorySearchRepository categorySearchRepository;

    private final ItemRepository itemRepository;

    private final ItemSearchRepository itemSearchRepository;

    private final PropertiesRepository propertiesRepository;

    private final PropertiesSearchRepository propertiesSearchRepository;

    private final SiteRepository siteRepository;

    private final SiteSearchRepository siteSearchRepository;

    private final UserPropertiesRepository userPropertiesRepository;

    private final UserPropertiesSearchRepository userPropertiesSearchRepository;

    private final UserRepository userRepository;

    private final UserSearchRepository userSearchRepository;

    private final ElasticsearchOperations elasticsearchTemplate;

    public ElasticsearchIndexService(
        UserRepository userRepository,
        UserSearchRepository userSearchRepository,
        CategoryRepository categoryRepository,
        CategorySearchRepository categorySearchRepository,
        ItemRepository itemRepository,
        ItemSearchRepository itemSearchRepository,
        PropertiesRepository propertiesRepository,
        PropertiesSearchRepository propertiesSearchRepository,
        SiteRepository siteRepository,
        SiteSearchRepository siteSearchRepository,
        UserPropertiesRepository userPropertiesRepository,
        UserPropertiesSearchRepository userPropertiesSearchRepository,
        ElasticsearchOperations elasticsearchTemplate) {
        this.userRepository = userRepository;
        this.userSearchRepository = userSearchRepository;
        this.categoryRepository = categoryRepository;
        this.categorySearchRepository = categorySearchRepository;
        this.itemRepository = itemRepository;
        this.itemSearchRepository = itemSearchRepository;
        this.propertiesRepository = propertiesRepository;
        this.propertiesSearchRepository = propertiesSearchRepository;
        this.siteRepository = siteRepository;
        this.siteSearchRepository = siteSearchRepository;
        this.userPropertiesRepository = userPropertiesRepository;
        this.userPropertiesSearchRepository = userPropertiesSearchRepository;
        this.elasticsearchTemplate = elasticsearchTemplate;
    }

    @Async
    @Timed
    public void reindexAll() {
        if (reindexLock.tryLock()) {
            try {
                reindexForClass(Category.class, categoryRepository, categorySearchRepository);
                reindexForClass(Item.class, itemRepository, itemSearchRepository);
                reindexForClass(Properties.class, propertiesRepository, propertiesSearchRepository);
                reindexForClass(Site.class, siteRepository, siteSearchRepository);
                reindexForClass(UserProperties.class, userPropertiesRepository, userPropertiesSearchRepository);
                reindexForClass(User.class, userRepository, userSearchRepository);

                log.info("Elasticsearch: Successfully performed reindexing");
            } finally {
                reindexLock.unlock();
            }
        } else {
            log.info("Elasticsearch: concurrent reindexing attempt");
        }
    }

    @SuppressWarnings("unchecked")
    private <T, ID extends Serializable> void reindexForClass(Class<T> entityClass, JpaRepository<T, ID> jpaRepository,
                                                              ElasticsearchRepository<T, ID> elasticsearchRepository) {
        elasticsearchTemplate.deleteIndex(entityClass);
        elasticsearchTemplate.createIndex(entityClass);

        elasticsearchTemplate.putMapping(entityClass);
        if (jpaRepository.count() > 0) {
            // if a JHipster entity field is the owner side of a many-to-many relationship, it should be loaded manually
            List<Method> relationshipGetters = Arrays.stream(entityClass.getDeclaredFields())
                .filter(field -> field.getType().equals(Set.class))
                .filter(field -> field.getAnnotation(ManyToMany.class) != null)
                .filter(field -> field.getAnnotation(ManyToMany.class).mappedBy().isEmpty())
                .filter(field -> field.getAnnotation(JsonIgnore.class) == null)
                .map(field -> {
                    try {
                        return new PropertyDescriptor((String)field.getName(), entityClass).getReadMethod();
                    } catch (IntrospectionException e) {
                        log.error("Error retrieving getter for class {}, field {}. Field will NOT be indexed",
                            entityClass.getSimpleName(), field.getName(), e);
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

            int size = 100;
            for (int i = 0; i <= jpaRepository.count() / size; i++) {
                Pageable page = PageRequest.of(i, size);
                log.info("Indexing page {} of {}, size {}", i, jpaRepository.count() / size, size);
                Page<T> results = jpaRepository.findAll(page);
                results.map(result -> {
                    // if there are any relationships to load, do it now
                    relationshipGetters.forEach(method -> {
                        try {
                            // eagerly load the relationship set
                            ((Set) method.invoke(result)).size();
                        } catch (Exception ex) {
                            log.error(ex.getMessage());
                        }
                    });
                    return result;
                });
                results.getContent().forEach(elasticsearchRepository::save);
            }
        }
        log.info("Elasticsearch: Indexed all rows for {}", entityClass.getSimpleName());
    }
}
