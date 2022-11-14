package db.gruppe20.abgabe3.backend.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Size;

@Entity(name = "Address")
@Table(name = "address", schema = "public")
public class Address {
    @Id
    @Column(name = "uid", nullable = false)
    private Integer id;

    @Size(max = 255)
    @Column(name = "street")
    private String street;

    @Column(name = "number")
    private Integer number;

    @Column(name = "zip")
    private Integer zip;

    public String toString(){
        return street + " " + number.toString() + ", " + zip.toString();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public Integer getZip() {
        return zip;
    }

    public void setZip(Integer zip) {
        this.zip = zip;
    }

}