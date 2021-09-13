package by.homesite.gator.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PropertiesMapperTest {

    private PropertiesMapper propertiesMapper;

    @BeforeEach
    public void setUp() {
        propertiesMapper = new PropertiesMapperImpl();
    }
}
