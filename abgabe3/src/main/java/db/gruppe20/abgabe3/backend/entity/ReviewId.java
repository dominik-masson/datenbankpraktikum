package db.gruppe20.abgabe3.backend.entity;

import org.hibernate.Hibernate;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

@Embeddable
public class ReviewId implements Serializable {
    private static final long serialVersionUID = -6282326990986053604L;
    @NotNull
    @Column(name = "customer", nullable = false)
    private Integer customer;

    @Size(max = 50)
    @NotNull
    @Column(name = "product", nullable = false, length = 50)
    private String product;

    @NotNull
    @Column(name = "\"timestamp\"", nullable = false)
    private Instant timestamp;

    public ReviewId(){

    }

    public Integer getCustomer() {
        return customer;
    }

    public void setCustomer(Integer customer) {
        this.customer = customer;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        ReviewId entity = (ReviewId) o;
        return Objects.equals(this.product, entity.product) &&
                Objects.equals(this.customer, entity.customer) &&
                Objects.equals(this.timestamp, entity.timestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(product, customer, timestamp);
    }

    public String toString(){
        return "[%s]%s: %s".formatted(this.timestamp.toString(), this.customer.toString(), this.product.toString());
    }

}