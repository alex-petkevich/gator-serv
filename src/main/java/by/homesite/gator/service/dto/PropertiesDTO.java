package by.homesite.gator.service.dto;

import java.io.Serializable;
import java.util.Objects;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link by.homesite.gator.domain.Properties} entity.
 */
public class PropertiesDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 255)
    private String name;

    private Boolean isActive;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PropertiesDTO)) {
            return false;
        }

        PropertiesDTO propertiesDTO = (PropertiesDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, propertiesDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PropertiesDTO{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", isActive='" + getIsActive() + "'" +
            "}";
    }
}
