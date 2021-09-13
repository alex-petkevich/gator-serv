package by.homesite.gator.repository.search;

import by.homesite.gator.domain.UserSites;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the {@link UserSites} entity.
 */
public interface UserSitesSearchRepository extends ElasticsearchRepository<UserSites, Long> {}
