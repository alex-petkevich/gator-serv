package by.homesite.gator.service.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link by.homesite.gator.domain.Rate} entity.
 */
public class RateDTO implements Serializable {

    private Long id;

    private String idname;

    private String code;

    private BigDecimal rate;

    private Instant active;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIdname() {
        return idname;
    }

    public void setIdname(String idname) {
        this.idname = idname;
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

    public Instant getActive() {
        return active;
    }

    public void setActive(Instant active) {
        this.active = active;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RateDTO)) {
            return false;
        }

        RateDTO rateDTO = (RateDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, rateDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "RateDTO{" +
            "id=" + getId() +
            ", idname='" + getIdname() + "'" +
            ", code='" + getCode() + "'" +
            ", rate=" + getRate() +
            ", active='" + getActive() + "'" +
            "}";
    }
}
