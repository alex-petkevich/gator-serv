package by.homesite.gator.domain;

import static org.assertj.core.api.Assertions.assertThat;

import by.homesite.gator.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class RateTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Rate.class);
        Rate rate1 = new Rate();
        rate1.setId(1L);
        Rate rate2 = new Rate();
        rate2.setId(rate1.getId());
        assertThat(rate1).isEqualTo(rate2);
        rate2.setId(2L);
        assertThat(rate1).isNotEqualTo(rate2);
        rate1.setId(null);
        assertThat(rate1).isNotEqualTo(rate2);
    }
}
