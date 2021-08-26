package by.homesite.gator.service.dto;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Objects;

/**
 * A DTO for the {@link by.homesite.gator.domain.UserNotifications} entity.
 */
public class UserNotificationsDTO implements Serializable {

    private Long id;

    private String contact;

    private Long total_qty;

    private ZonedDateTime lastSent;

    private Long userId;

    private Long notificationId;

    private Long userSearchesId;

    private boolean active;

    public Long getUserId()
    {
        return userId;
    }

    public Long getNotificationId()
    {
        return notificationId;
    }

    public void setNotificationId(Long notificationId)
    {
        this.notificationId = notificationId;
    }

    public void setUserId(Long userId)
    {
        this.userId = userId;
    }

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

    public Long getTotal_qty()
    {
        return total_qty;
    }

    public void setTotal_qty(Long total_qty)
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

    public Long getUserSearchesId()
    {
        return userSearchesId;
    }

    public void setUserSearchesId(Long userSearchesId)
    {
        this.userSearchesId = userSearchesId;
    }

    public boolean isActive()
    {
        return active;
    }

    public void setActive(boolean active)
    {
        this.active = active;
    }

    @Override
    public String toString()
    {
        return "UserNotificationsDTO{" +
            "id=" + id +
            ", contact='" + contact + '\'' +
            ", total_qty=" + total_qty +
            ", lastSent=" + lastSent +
            '}';
    }
}
