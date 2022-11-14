package db.gruppe20.abgabe3.backend.entity;

import org.hibernate.Hibernate;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class AssociatedCompanyId implements Serializable {
    private static final long serialVersionUID = 8301041062669119193L;
    @NotNull
    @Column(name = "company", nullable = false)
    private Integer company;

    @Size(max = 50)
    @NotNull
    @Column(name = "product", nullable = false, length = 50)
    private String product;

    public Integer getCompany() {
        return company;
    }

    public void setCompany(Integer company) {
        this.company = company;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        AssociatedCompanyId entity = (AssociatedCompanyId) o;
        return Objects.equals(this.product, entity.product) &&
                Objects.equals(this.company, entity.company);
    }

    @Override
    public int hashCode() {
        return Objects.hash(product, company);
    }

}