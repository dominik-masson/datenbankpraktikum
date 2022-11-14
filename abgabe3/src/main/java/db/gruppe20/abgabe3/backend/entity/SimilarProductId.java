package db.gruppe20.abgabe3.backend.entity;

import org.hibernate.Hibernate;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class SimilarProductId implements Serializable {
    private static final long serialVersionUID = 4332277117573826733L;
    @Size(max = 50)
    @NotNull
    @Column(name = "product1", nullable = false, length = 50)
    private String product1;

    @Size(max = 50)
    @NotNull
    @Column(name = "product2", nullable = false, length = 50)
    private String product2;

    public String getProduct1() {
        return product1;
    }

    public void setProduct1(String product1) {
        this.product1 = product1;
    }

    public String getProduct2() {
        return product2;
    }

    public void setProduct2(String product2) {
        this.product2 = product2;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        SimilarProductId entity = (SimilarProductId) o;
        return Objects.equals(this.product2, entity.product2) &&
                Objects.equals(this.product1, entity.product1);
    }

    @Override
    public int hashCode() {
        return Objects.hash(product2, product1);
    }

}