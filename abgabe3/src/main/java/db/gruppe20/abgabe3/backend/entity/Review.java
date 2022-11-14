package db.gruppe20.abgabe3.backend.entity;

import org.hibernate.Hibernate;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity(name = "Review")
@Table(name = "review", schema = "public")
public class Review {
    @EmbeddedId
    private ReviewId id;

    @MapsId("customer")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "customer", nullable = false)
    private Customer customer;

    @MapsId("product")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "product", nullable = false)
    private Product product;

    @NotNull
    @Column(name = "description", nullable = false)
    @Type(type = "org.hibernate.type.TextType")
    private String description;

    @NotNull
    @Column(name = "summary", nullable = false)
    @Type(type = "org.hibernate.type.TextType")
    private String summary;

    @Column(name = "points")
    private Integer points;

    @Column(name = "helpful")
    private Integer helpful;

    public Review(String customerName, String productId, String description, int points) {
        this.customer = new Customer(customerName);
        this.product = new Product(productId);
        this.description = description;
        this.points = points;
    }

    public Review() {

    }

    public void initializeReview(){
        Hibernate.initialize(this);
    }

    public ReviewId getId() {
        return id;
    }

    public void setId(ReviewId id) {
        this.id = id;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public Integer getPoints() {
        return points;
    }

    public void setPoints(Integer points) {
        this.points = points;
    }

    public Integer getHelpful() {
        return helpful;
    }

    public void setHelpful(Integer helpful) {
        this.helpful = helpful;
    }

    public String toString(){
        return  "[Customer: " + this.customer.getUsername() + " Description: " + this.description + " Summary: " + this.summary + " Points: " + this.points + "]";
    }

}