package by.homesite.gator.repository.search;

import by.homesite.gator.domain.Item;
import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Spring Data Elasticsearch repository for the {@link Item} entity.
 */
public interface ItemSearchRepository extends ElasticsearchRepository<Item, Long> {}
