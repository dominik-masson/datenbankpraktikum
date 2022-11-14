package db.gruppe20.abgabe3.backend.entity;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Entity(name = "Dvd")
@Table(name = "dvd", schema = "public")
public class Dvd {
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

    @Size(max = 255)
    @Column(name = "format")
    private String format;

    @Column(name = "runtime")
    private Integer runtime;

    @Column(name = "region_code")
    private Integer regionCode;

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

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public Integer getRuntime() {
        return runtime;
    }

    public void setRuntime(Integer runtime) {
        this.runtime = runtime;
    }

    public Integer getRegionCode() {
        return regionCode;
    }

    public void setRegionCode(Integer regionCode) {
        this.regionCode = regionCode;
    }

    public LocalDate getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(LocalDate releaseDate) {
        this.releaseDate = releaseDate;
    }

}