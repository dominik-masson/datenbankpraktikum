package db.gruppe20.abgabe3.backend.entity;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Entity(name = "Cd")
@Table(name = "cd", schema = "public")
public class Cd {
    @Id
    @Size(max = 50)
    @Column(name = "asin", nullable = false, length = 50)
    private String id;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "asin", nullable = false)
    private Product product;

    @Size(max = 50)
    @NotNull
    @Column(name = "ean", nullable = false, length = 50)
    private String ean;

    @Column(name = "release_date")
    private LocalDate releaseDate;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public String getEan() {
        return ean;
    }

    public void setEan(String ean) {
        this.ean = ean;
    }

    public LocalDate getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(LocalDate releaseDate) {
        this.releaseDate = releaseDate;
    }

/*
    TODO [JPA Buddy] create field to map the 'tracks' column
     Available actions: Define target Java type | Uncomment as is | Remove column mapping
    @Column(name = "tracks", columnDefinition = "text[]")
    private Object tracks;
*/
}