package by.homesite.gator.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RateMapperTest {

    private RateMapper rateMapper;

    @BeforeEach
    public void setUp() {
        rateMapper = new RateMapperImpl();
    }
}
