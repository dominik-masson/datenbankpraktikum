package db.gruppe20.abgabe3.backend.entity;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import javax.validation.constraints.Size;

@Entity(name = "ProductsInStore")
@Table(name = "products_in_store", schema = "public")
public class ProductsInStore {
    @EmbeddedId
    private ProductsInStoreId id;

    @MapsId("store")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "store", nullable = false)
    private Store store;

    @MapsId("product")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "product", nullable = false)
    private Product product;

    @Column(name = "price")
    private Double price;

    @Size(max = 5)
    @Column(name = "currency", length = 5)
    private String currency;

    @Column(name = "availability")
    private Boolean availability;

    public ProductsInStoreId getId() {
        return id;
    }

    public void setId(ProductsInStoreId id) {
        this.id = id;
    }

    public Store getStore() {
        return store;
    }

    public void setStore(Store store) {
        this.store = store;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Boolean getAvailability() {
        return availability;
    }

    public void setAvailability(Boolean availability) {
        this.availability = availability;
    }

    public String toString(){
        return  this.store.getName() + ": " + String.format("%.2f",this.getPrice()) + this.getCurrency() + " Zustand: " + this.id.getCondition();
    }

}