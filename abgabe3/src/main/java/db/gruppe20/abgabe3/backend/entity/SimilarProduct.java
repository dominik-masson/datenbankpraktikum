package db.gruppe20.abgabe3.backend.entity;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;

@Entity(name = "SimilarProduct")
@Table(name = "similar_product", schema = "public")
public class SimilarProduct {
    @EmbeddedId
    private SimilarProductId id;

    @MapsId("product1")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "product1", nullable = false)
    private Product product1;

    @MapsId("product2")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "product2", nullable = false)
    private Product product2;

    public SimilarProductId getId() {
        return id;
    }

    public void setId(SimilarProductId id) {
        this.id = id;
    }

    public Product getProduct1() {
        return product1;
    }

    public void setProduct1(Product product1) {
        this.product1 = product1;
    }

    public Product getProduct2() {
        return product2;
    }

    public void setProduct2(Product product2) {
        this.product2 = product2;
    }

}