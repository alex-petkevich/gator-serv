package by.homesite.gator.service.dto;

import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link by.homesite.gator.domain.UserProperties} entity.
 */
public class UserPropertiesDTO implements Serializable {

    private Long id;

    private String value;

    private Long userId;

    private String userLogin;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserLogin() {
        return userLogin;
    }

    public void setUserLogin(String userLogin) {
        this.userLogin = userLogin;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof UserPropertiesDTO)) {
            return false;
        }

        UserPropertiesDTO userPropertiesDTO = (UserPropertiesDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, userPropertiesDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "UserPropertiesDTO{" +
            "id=" + getId() +
            ", value='" + getValue() + "'" +
            ", user=" + getUserId() +
            ", user='" + getUserLogin() + "'" +
            "}";
    }
}
