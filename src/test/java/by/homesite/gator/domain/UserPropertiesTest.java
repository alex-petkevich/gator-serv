package by.homesite.gator.domain;

import static org.assertj.core.api.Assertions.assertThat;

import by.homesite.gator.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class UserPropertiesTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(UserProperties.class);
        UserProperties userProperties1 = new UserProperties();
        userProperties1.setId(1L);
        UserProperties userProperties2 = new UserProperties();
        userProperties2.setId(userProperties1.getId());
        assertThat(userProperties1).isEqualTo(userProperties2);
        userProperties2.setId(2L);
        assertThat(userProperties1).isNotEqualTo(userProperties2);
        userProperties1.setId(null);
        assertThat(userProperties1).isNotEqualTo(userProperties2);
    }
}
