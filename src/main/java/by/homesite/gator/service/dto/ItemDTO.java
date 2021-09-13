package by.homesite.gator.service.dto;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Objects;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link by.homesite.gator.domain.Item} entity.
 */
public class ItemDTO implements Serializable {

    private Long id;

    @NotNull
    private String title;

    private String description;

    private Float price;

    private String link;

    private String ownerName;

    private String ownerLink;

    private String image;

    private Boolean active;

    private ZonedDateTime createdAt;

    private ZonedDateTime updatedAt;

    private ZonedDateTime deletedAt;

    private Long categoryId;

    private String categoryName;

    private Long nativeId;

    private Integer type;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Float getPrice() {
        return price;
    }

    public void setPrice(Float price) {
        this.price = price;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getOwnerLink() {
        return ownerLink;
    }

    public void setOwnerLink(String ownerLink) {
        this.ownerLink = ownerLink;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Boolean isActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(ZonedDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public ZonedDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(ZonedDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public ZonedDateTime getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(ZonedDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Long getNativeId() {
        return nativeId;
    }

    public void setNativeId(Long nativeId) {
        this.nativeId = nativeId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ItemDTO itemDTO = (ItemDTO) o;
        if (itemDTO.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), itemDTO.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ItemDTO{" +
            "id=" + getId() +
            ", title='" + getTitle() + "'" +
            ", description='" + getDescription() + "'" +
            ", price=" + getPrice() +
            ", link='" + getLink() + "'" +
            ", ownerName='" + getOwnerName() + "'" +
            ", ownerLink='" + getOwnerLink() + "'" +
            ", image='" + getImage() + "'" +
            ", active='" + isActive() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            ", updatedAt='" + getUpdatedAt() + "'" +
            ", deletedAt='" + getDeletedAt() + "'" +
            ", category=" + getCategoryId() +
            ", category='" + getCategoryName() + "'" +
            ", nativeId='" + getNativeId() + "'" +
            ", type='" + getType() + "'" +
            "}";
    }
}
