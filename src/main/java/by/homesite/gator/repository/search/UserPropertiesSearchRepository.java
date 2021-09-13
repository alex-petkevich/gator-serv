package by.homesite.gator.repository.search;

import by.homesite.gator.domain.UserProperties;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the {@link UserProperties} entity.
 */
public interface UserPropertiesSearchRepository extends ElasticsearchRepository<UserProperties, Long> {}
