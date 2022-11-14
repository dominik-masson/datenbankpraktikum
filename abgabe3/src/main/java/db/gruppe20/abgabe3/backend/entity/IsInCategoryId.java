package db.gruppe20.abgabe3.backend.entity;

import org.hibernate.Hibernate;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class IsInCategoryId implements Serializable {
    private static final long serialVersionUID = 5727493220063076753L;
    @Size(max = 50)
    @NotNull
    @Column(name = "product", nullable = false, length = 50)
    private String product;

    @NotNull
    @Column(name = "category", nullable = false)
    private Integer category;

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public Integer getCategory() {
        return category;
    }

    public void setCategory(Integer category) {
        this.category = category;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        IsInCategoryId entity = (IsInCategoryId) o;
        return Objects.equals(this.product, entity.product) &&
                Objects.equals(this.category, entity.category);
    }

    @Override
    public int hashCode() {
        return Objects.hash(product, category);
    }

}