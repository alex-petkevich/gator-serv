package by.homesite.gator.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import by.homesite.gator.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class UserPropertiesDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(UserPropertiesDTO.class);
        UserPropertiesDTO userPropertiesDTO1 = new UserPropertiesDTO();
        userPropertiesDTO1.setId(1L);
        UserPropertiesDTO userPropertiesDTO2 = new UserPropertiesDTO();
        assertThat(userPropertiesDTO1).isNotEqualTo(userPropertiesDTO2);
        userPropertiesDTO2.setId(userPropertiesDTO1.getId());
        assertThat(userPropertiesDTO1).isEqualTo(userPropertiesDTO2);
        userPropertiesDTO2.setId(2L);
        assertThat(userPropertiesDTO1).isNotEqualTo(userPropertiesDTO2);
        userPropertiesDTO1.setId(null);
        assertThat(userPropertiesDTO1).isNotEqualTo(userPropertiesDTO2);
    }
}
