package by.homesite.gator.domain;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.elasticsearch.annotations.FieldType;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * A UserProperties.
 */
@Entity
@Table(name = "user_searches")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class UserSearches implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "payload")
    private String payload;

    @ManyToOne
    @JsonIgnoreProperties("userSearches")
    private User user;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public UserSearches name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public User getUser() {
        return user;
    }

    public UserSearches user(User user) {
        this.user = user;
        return this;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getPayload()
    {
        return payload;
    }

    public void setPayload(String payload)
    {
        this.payload = payload;
    }

    public UserSearches payload(String payload) {
        this.payload = payload;
        return this;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        UserSearches that = (UserSearches) o;
        return id.equals(that.id) && name.equals(that.name) && Objects.equals(payload, that.payload);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(id, name, payload);
    }

    @Override
    public String toString()
    {
        return "UserSearches{" +
            "id=" + id +
            ", name='" + name + '\'' +
            ", payload='" + payload + '\'' +
            ", user=" + user +
            '}';
    }
}
