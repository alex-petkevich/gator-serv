package by.homesite.gator.repository.search;

import by.homesite.gator.domain.Rate;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the {@link Rate} entity.
 */
public interface RateSearchRepository extends ElasticsearchRepository<Rate, Long> {}
