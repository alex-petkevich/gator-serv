package by.homesite.gator.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import javax.persistence.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.elasticsearch.annotations.FieldType;

/**
 * A Rate.
 */
@Entity
@Table(name = "rate")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "rate")
public class Rate implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @Column(name = "idname")
    private String idname;

    @Column(name = "code")
    private String code;

    @Column(name = "rate", precision = 21, scale = 2)
    private BigDecimal rate;

    @Column(name = "active")
    private Instant active;

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Rate id(Long id) {
        this.id = id;
        return this;
    }

    public String getIdname() {
        return this.idname;
    }

    public Rate idname(String idname) {
        this.idname = idname;
        return this;
    }

    public void setIdname(String idname) {
        this.idname = idname;
    }

    public String getCode() {
        return this.code;
    }

    public Rate code(String code) {
        this.code = code;
        return this;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public BigDecimal getRate() {
        return this.rate;
    }

    public Rate rate(BigDecimal rate) {
        this.rate = rate;
        return this;
    }

    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }

    public Instant getActive() {
        return this.active;
    }

    public Rate active(Instant active) {
        this.active = active;
        return this;
    }

    public void setActive(Instant active) {
        this.active = active;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

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
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
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
