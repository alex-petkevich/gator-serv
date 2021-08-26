package by.homesite.gator.domain;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.springframework.data.elasticsearch.annotations.FieldType;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * A UserProperties.
 */
@Entity
@Table(name = "user_notifications")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class UserNotifications implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @org.springframework.data.elasticsearch.annotations.Field(type = FieldType.Keyword)
    private Long id;

    @Column(name = "contact")
    private String contact;

    @Column(name = "total_qty")
    private Integer total_qty;

    @Column(name = "last_sent")
    private ZonedDateTime lastSent;

    @Column(name = "active")
    private Boolean active;

    @JsonIgnore
    @ManyToOne
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private User user;

    @JsonIgnore
    @ManyToOne
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Notification notification;

    @JoinColumn
    @JsonIgnore
    @NotFound(action=NotFoundAction.IGNORE)
    @ManyToOne
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private UserSearches userSearches;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContact()
    {
        return contact;
    }

    public void setContact(String contact)
    {
        this.contact = contact;
    }

    public Integer getTotal_qty()
    {
        return total_qty;
    }

    public void setTotal_qty(Integer total_qty)
    {
        this.total_qty = total_qty;
    }

    public ZonedDateTime getLastSent()
    {
        return lastSent;
    }

    public void setLastSent(ZonedDateTime lastSent)
    {
        this.lastSent = lastSent;
    }

    public User getUser()
    {
        return user;
    }

    public void setUser(User user)
    {
        this.user = user;
    }

    public Notification getNotification()
    {
        return notification;
    }

    public void setNotification(Notification notification)
    {
        this.notification = notification;
    }

    public UserSearches getUserSearches()
    {
        return userSearches;
    }

    public void setUserSearches(UserSearches userSearches)
    {
        this.userSearches = userSearches;
    }

    public Boolean getActive()
    {
        return active;
    }

    public void setActive(Boolean active)
    {
        this.active = active;
    }

    @Override
    public String toString()
    {
        return "UserNotifications{" +
            "id=" + id +
            ", contact='" + contact + '\'' +
            ", total_qty=" + total_qty +
            ", lastSent=" + lastSent +
            ", user=" + user +
            ", notification=" + notification +
            '}';
    }
}
