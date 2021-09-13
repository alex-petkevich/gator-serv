package by.homesite.gator.domain;

import static org.assertj.core.api.Assertions.assertThat;

import by.homesite.gator.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class UserSitesTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(UserSites.class);
        UserSites userSites1 = new UserSites();
        userSites1.setId(1L);
        UserSites userSites2 = new UserSites();
        userSites2.setId(userSites1.getId());
        assertThat(userSites1).isEqualTo(userSites2);
        userSites2.setId(2L);
        assertThat(userSites1).isNotEqualTo(userSites2);
        userSites1.setId(null);
        assertThat(userSites1).isNotEqualTo(userSites2);
    }
}
