package db.gruppe20.abgabe3.backend.entity;

import org.hibernate.Hibernate;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.Set;

@Entity(name = "Product")
@Table(name = "product", schema = "public")
public class Product {
    @Id
    @Size(max = 50)
    @Column(name = "asin", nullable = false, length = 50)
    private String id;

    @Size(max = 512)
    @NotNull
    @Column(name = "title", nullable = false, length = 512)
    private String title;

    @Column(name = "rating")
    private Double rating;

    @Column(name = "salesrank")
    private Integer salesrank;

    @Size(max = 512)
    @Column(name = "image", length = 512)
    private String image;

    @ManyToMany
    @JoinTable(name = "similar_product",
    joinColumns = {@JoinColumn(name = "product1")},
    inverseJoinColumns = {@JoinColumn(name = "product2")})
    private Set<Product> similarProduct;

    @OneToOne(mappedBy = "product")
    private LowestPrice lowestPrice;

    @OneToMany(mappedBy = "product")
    private List<ProductsInStore> offers;

    public Product(){}

    public Product(String productId){
        this.id = productId;
    }
    public Product(String id, String title, Double rating, Integer salesrank, String image) {
        this.id = id;
        this.title = title;
        this.rating = rating;
        this.salesrank = salesrank;
        this.image = image;
    }

    /*
    fetch object in proxy/persistence context
    lazy load the data
     */
    public void initialize(){
        Hibernate.initialize(this);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public Integer getSalesrank() {
        return salesrank;
    }

    public void setSalesrank(Integer salesrank) {
        this.salesrank = salesrank;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Set<Product> getSimilarProduct() {
        return similarProduct;
    }

    public void setSimilarProduct(Set<Product> similarProduct) {
        this.similarProduct = similarProduct;
    }

    public LowestPrice getLowestPrice() {
        return lowestPrice;
    }

    public void setLowestPrice(LowestPrice lowestPrice) {
        this.lowestPrice = lowestPrice;
    }


    public List<ProductsInStore> getOffers() {
        return offers;
    }

    public void setOffers(List<ProductsInStore> offers) {
        this.offers = offers;
    }

    public String toString(){
        return "[" + this.id + "]: " + this.title;
    }
}