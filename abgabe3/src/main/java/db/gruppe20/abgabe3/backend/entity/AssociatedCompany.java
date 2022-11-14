package db.gruppe20.abgabe3.backend.entity;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import javax.validation.constraints.Size;

@Entity(name = "AssociatedCompany")
@Table(name = "associated_company", schema = "public")
public class AssociatedCompany {
    @EmbeddedId
    private AssociatedCompanyId id;

    @MapsId("company")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "company", nullable = false)
    private Company company;

    @MapsId("product")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "product", nullable = false)
    private Product product;

    @Size(max = 128)
    @Column(name = "role", length = 128)
    private String role;

    public AssociatedCompanyId getId() {
        return id;
    }

    public void setId(AssociatedCompanyId id) {
        this.id = id;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

}