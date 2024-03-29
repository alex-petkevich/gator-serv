package by.homesite.gator.repository.search;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;

/**
 * Configure a Mock version of {@link PropertiesSearchRepository} to test the
 * application without starting Elasticsearch.
 */
@Configuration
public class PropertiesSearchRepositoryMockConfiguration {

    @MockBean
    private PropertiesSearchRepository mockPropertiesSearchRepository;
}
