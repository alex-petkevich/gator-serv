package by.homesite.gator.service.dto;
import java.io.Serializable;
import java.time.ZonedDateTime;

/**
 * A DTO for the {@link by.homesite.gator.domain.UserNotifications} entity.
 */
public class UserNotificationsDTO implements Serializable {

    private Long id;

    private String contact;

    private Long totalQty;

    private ZonedDateTime lastSent;

    private Long userId;

    private Long notificationId;

    private String notificationName;

    private Long userSearchesId;

    private String userSearchesName;

    private Boolean isActive;

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

    public Long getTotalQty()
    {
        return totalQty;
    }

    public void setTotalQty(Long totalQty)
    {
        this.totalQty = totalQty;
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

    public Boolean getIsActive()
    {
        return isActive;
    }

    public void setIsActive(Boolean active)
    {
        isActive = active;
    }

    public String getNotificationName()
    {
        return notificationName;
    }

    public void setNotificationName(String notificationName)
    {
        this.notificationName = notificationName;
    }

    public String getUserSearchesName()
    {
        return userSearchesName;
    }

    public void setUserSearchesName(String userSearchesName)
    {
        this.userSearchesName = userSearchesName;
    }

    @Override
    public String toString()
    {
        return "UserNotificationsDTO{" +
            "id=" + id +
            ", contact='" + contact + '\'' +
            ", total_qty=" + totalQty +
            ", lastSent=" + lastSent +
            '}';
    }
}
