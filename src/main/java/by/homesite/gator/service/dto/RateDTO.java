package by.homesite.gator.service.dto;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * A DTO for the {@link by.homesite.gator.domain.Rate} entity.
 */
public class RateDTO implements Serializable {

    private Long id;

    private String name;

    private String code;

    private String mark;

    private BigDecimal rate;

    private Boolean active;

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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public BigDecimal getRate() {
        return rate;
    }

    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public String getMark() {
        return mark;
    }

    public void setMark(String mark) {
        this.mark = mark;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        RateDTO rateDTO = (RateDTO) o;
        if (rateDTO.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), rateDTO.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "RateDTO{" +
            "id=" + getId() +
            ", idname='" + getName() + "'" +
            ", code='" + getCode() + "'" +
            ", rate=" + getRate() +
            ", mark=" + getMark() +
            ", active='" + getActive() + "'" +
            "}";
    }
}
