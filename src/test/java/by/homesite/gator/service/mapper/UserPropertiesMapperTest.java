package by.homesite.gator.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class UserPropertiesMapperTest {

    private UserPropertiesMapper userPropertiesMapper;

    @BeforeEach
    public void setUp() {
        userPropertiesMapper = new UserPropertiesMapperImpl();
    }
}
