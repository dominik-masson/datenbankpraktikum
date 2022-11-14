package db.gruppe20.abgabe3.backend.entity;

import org.hibernate.annotations.Immutable;

import javax.persistence.*;
import java.io.Serializable;

@Immutable
@Entity(name = "lowest_price")
public class LowestPrice implements Serializable {

    @Id
    @OneToOne
    @JoinColumn(name = "asin")
    private Product product;

    @Column(name = "min")
    private Integer price;

    public LowestPrice(){}


    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }
}