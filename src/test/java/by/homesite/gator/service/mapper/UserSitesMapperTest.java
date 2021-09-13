package by.homesite.gator.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class UserSitesMapperTest {

    private UserSitesMapper userSitesMapper;

    @BeforeEach
    public void setUp() {
        userSitesMapper = new UserSitesMapperImpl();
    }
}
