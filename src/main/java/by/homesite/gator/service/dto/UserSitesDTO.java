package by.homesite.gator.service.dto;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link by.homesite.gator.domain.UserSites} entity.
 */
public class UserSitesDTO implements Serializable {

    private Long id;


    private Long userId;

    private String userLogin;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        UserSitesDTO userSitesDTO = (UserSitesDTO) o;
        if (userSitesDTO.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), userSitesDTO.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "UserSitesDTO{" +
            "id=" + getId() +
            ", user=" + getUserId() +
            ", user='" + getUserLogin() + "'" +
            "}";
    }
}
