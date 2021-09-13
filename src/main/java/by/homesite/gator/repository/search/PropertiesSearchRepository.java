package by.homesite.gator.repository.search;

import by.homesite.gator.domain.Properties;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the {@link Properties} entity.
 */
public interface PropertiesSearchRepository extends ElasticsearchRepository<Properties, Long> {
}
