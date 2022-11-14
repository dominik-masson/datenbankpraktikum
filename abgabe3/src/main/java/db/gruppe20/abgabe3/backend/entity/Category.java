package db.gruppe20.abgabe3.backend.entity;

import org.hibernate.Hibernate;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.List;

@Entity(name = "Category")
@Table(name = "category", schema = "public")
public class Category {
    @Id
    @Column(name = "uid", nullable = false)
    private Integer id;

    @Size(max = 255)
    @Column(name = "name")
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_category")
    private Category parentCategory;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "category",
            joinColumns = @JoinColumn(name = "parent_category"),
            inverseJoinColumns = @JoinColumn(name = "uid"))
    private List<Category> subCategories;

    @ManyToMany
    @JoinTable(name = "is_in_category",
            joinColumns = {@JoinColumn(name = "category")},
            inverseJoinColumns ={@JoinColumn(name = "product")})
    private List<Product> productList;

    public void initializeSubCategories() {
        Hibernate.initialize(this);
        subCategories.forEach(Hibernate::initialize);
        //subCategories.forEach(Category::initializeSubCategories);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Category getParentCategory() {
        return parentCategory;
    }

    public void setParentCategory(Category parentCategory) {
        this.parentCategory = parentCategory;
    }

    public List<Category> getSubCategories() {
        return subCategories;
    }

    public void setSubCategories(List<Category> subCategories) {
        this.subCategories = subCategories;
    }

    public String toString() {
        return this.name + " " + this.subCategories + " ";
    }

    public List<Product> getProductList() {
        return productList;
    }

    public void setProductList(List<Product> productList) {
        this.productList = productList;
    }
}