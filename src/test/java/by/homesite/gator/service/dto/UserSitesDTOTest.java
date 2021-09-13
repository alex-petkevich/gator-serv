package by.homesite.gator.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import by.homesite.gator.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class UserSitesDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(UserSitesDTO.class);
        UserSitesDTO userSitesDTO1 = new UserSitesDTO();
        userSitesDTO1.setId(1L);
        UserSitesDTO userSitesDTO2 = new UserSitesDTO();
        assertThat(userSitesDTO1).isNotEqualTo(userSitesDTO2);
        userSitesDTO2.setId(userSitesDTO1.getId());
        assertThat(userSitesDTO1).isEqualTo(userSitesDTO2);
        userSitesDTO2.setId(2L);
        assertThat(userSitesDTO1).isNotEqualTo(userSitesDTO2);
        userSitesDTO1.setId(null);
        assertThat(userSitesDTO1).isNotEqualTo(userSitesDTO2);
    }
}
