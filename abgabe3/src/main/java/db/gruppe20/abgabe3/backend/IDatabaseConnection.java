package db.gruppe20.abgabe3.backend;

import db.gruppe20.abgabe3.backend.entity.*;

import java.util.List;

public interface IDatabaseConnection {

    public void init();

    public void finish();

    public Product getProduct(String productId);

    public List<Product> getProducts(String pattern);

    public Category getCategoryTree();

    public List<Product> getProductsByCategoryPath(List<String> path);

    public List<Product> getTopProducts(int k);

    public List<Product> getSimilarCheaperProduct(String productId);

    public List<Review> getReviews(String productId);
    public Review addNewReview(String customerName, String productId, String description, int points);

    public List<Customer> getTrolls(double threshold);

    public List<ProductsInStore> getOffers(String productId);


}
