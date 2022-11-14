package db.gruppe20.abgabe3.backend.entity;

import org.hibernate.Hibernate;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class ProductsInStoreId implements Serializable {
    private static final long serialVersionUID = -519649689715596169L;
    @NotNull
    @Column(name = "store", nullable = false)
    private Integer store;

    @Size(max = 50)
    @NotNull
    @Column(name = "product", nullable = false, length = 50)
    private String product;

    @Size(max = 50)
    @NotNull
    @Column(name = "condition", nullable = false, length = 50)
    private String condition;

    public String toString() {
        return "[%s]%s : %s".formatted(this.store, this.product, this.condition);
    }

    public Integer getStore() {
        return store;
    }

    public void setStore(Integer store) {
        this.store = store;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        ProductsInStoreId entity = (ProductsInStoreId) o;
        return Objects.equals(this.product, entity.product) &&
                Objects.equals(this.condition, entity.condition) &&
                Objects.equals(this.store, entity.store);
    }

    @Override
    public int hashCode() {
        return Objects.hash(product, condition, store);
    }

}