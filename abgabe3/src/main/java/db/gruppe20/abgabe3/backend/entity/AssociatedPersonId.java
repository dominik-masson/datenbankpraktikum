package db.gruppe20.abgabe3.backend.entity;

import org.hibernate.Hibernate;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class AssociatedPersonId implements Serializable {
    private static final long serialVersionUID = 7659414849178313964L;
    @NotNull
    @Column(name = "person", nullable = false)
    private Integer person;

    @Size(max = 50)
    @NotNull
    @Column(name = "product", nullable = false, length = 50)
    private String product;

    @Size(max = 128)
    @NotNull
    @Column(name = "title", nullable = false, length = 128)
    private String title;

    public Integer getPerson() {
        return person;
    }

    public void setPerson(Integer person) {
        this.person = person;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        AssociatedPersonId entity = (AssociatedPersonId) o;
        return Objects.equals(this.product, entity.product) &&
                Objects.equals(this.person, entity.person) &&
                Objects.equals(this.title, entity.title);
    }

    @Override
    public int hashCode() {
        return Objects.hash(product, person, title);
    }

}