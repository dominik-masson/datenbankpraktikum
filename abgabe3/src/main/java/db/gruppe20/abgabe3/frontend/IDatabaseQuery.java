package db.gruppe20.abgabe3.frontend;

import db.gruppe20.abgabe3.frontend.entity.*;


import java.util.List;

public interface IDatabaseQuery {

    public void init();

    public void finish();

    public ProductRecord getProduct(String productId);

    public List<ProductRecord> getProducts(String pattern);

    public CategoryRecord getCategoryTree();

    public List<ProductRecord> getProductsByCategoryPath(String path);

    public List<ProductRecord> getTopProducts(int k);

    public List<ProductRecord> getSimilarCheaperProduct(String productId);
    public List<ReviewRecord> getReviews(String productId);
    public ReviewRecord addNewReview(String username, String productId, String description, int points);

    public List<CustomerRecord> getTrolls(float threshold);

    public List<OfferRecord> getOffers(String productId);


}
