package by.homesite.gator.service.dto;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link by.homesite.gator.domain.UserSearches} entity.
 */
public class UserSearchesDTO implements Serializable {

    private Long id;

    private String name;

    private Long userId;

    private String payload;

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

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        UserSearchesDTO that = (UserSearchesDTO) o;
        return id.equals(that.id) && name.equals(that.name) && userId.equals(that.userId) && Objects.equals(payload, that.payload);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(id, name, userId, payload);
    }
}
