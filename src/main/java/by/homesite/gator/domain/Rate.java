package by.homesite.gator.domain;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;

import org.springframework.data.elasticsearch.annotations.FieldType;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;

/**
 * A Rate.
 */
@Entity
@Table(name = "rate")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "rate")
public class Rate implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @org.springframework.data.elasticsearch.annotations.Field(type = FieldType.Keyword)
    private Long id;

    @Column(name = "idname")
    private String idname;

    @Column(name = "code")
    private String code;

    @Column(name = "rate", precision = 21, scale = 2)
    private BigDecimal rate;

    @Column(name = "active")
    private Instant active;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIdname() {
        return idname;
    }

    public Rate idname(String idname) {
        this.idname = idname;
        return this;
    }

    public void setIdname(String idname) {
        this.idname = idname;
    }

    public String getCode() {
        return code;
    }

    public Rate code(String code) {
        this.code = code;
        return this;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public BigDecimal getRate() {
        return rate;
    }

    public Rate rate(BigDecimal rate) {
        this.rate = rate;
        return this;
    }

    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }

    public Instant getActive() {
        return active;
    }

    public Rate active(Instant active) {
        this.active = active;
        return this;
    }

    public void setActive(Instant active) {
        this.active = active;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Rate)) {
            return false;
        }
        return id != null && id.equals(((Rate) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "Rate{" +
            "id=" + getId() +
            ", idname='" + getIdname() + "'" +
            ", code='" + getCode() + "'" +
            ", rate=" + getRate() +
            ", active='" + getActive() + "'" +
            "}";
    }
}
