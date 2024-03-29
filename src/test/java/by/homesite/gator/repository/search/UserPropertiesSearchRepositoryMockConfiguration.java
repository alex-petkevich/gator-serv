package by.homesite.gator.repository.search;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;

/**
 * Configure a Mock version of {@link UserPropertiesSearchRepository} to test the
 * application without starting Elasticsearch.
 */
@Configuration
public class UserPropertiesSearchRepositoryMockConfiguration {

    @MockBean
    private UserPropertiesSearchRepository mockUserPropertiesSearchRepository;
}
