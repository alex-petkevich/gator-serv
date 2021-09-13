package by.homesite.gator.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import by.homesite.gator.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class PropertiesDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(PropertiesDTO.class);
        PropertiesDTO propertiesDTO1 = new PropertiesDTO();
        propertiesDTO1.setId(1L);
        PropertiesDTO propertiesDTO2 = new PropertiesDTO();
        assertThat(propertiesDTO1).isNotEqualTo(propertiesDTO2);
        propertiesDTO2.setId(propertiesDTO1.getId());
        assertThat(propertiesDTO1).isEqualTo(propertiesDTO2);
        propertiesDTO2.setId(2L);
        assertThat(propertiesDTO1).isNotEqualTo(propertiesDTO2);
        propertiesDTO1.setId(null);
        assertThat(propertiesDTO1).isNotEqualTo(propertiesDTO2);
    }
}
