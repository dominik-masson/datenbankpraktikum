package db.gruppe20.abgabe3.bridge;

import db.gruppe20.abgabe3.backend.DatabaseConnector;
import db.gruppe20.abgabe3.backend.entity.*;
import db.gruppe20.abgabe3.frontend.IDatabaseQuery;
import db.gruppe20.abgabe3.frontend.entity.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class DatabaseBridge implements IDatabaseQuery {

    private static DatabaseBridge databaseBridgeInstance;
    private DatabaseConnector databaseConnector;

    public static DatabaseBridge getInstance(){

        if(databaseBridgeInstance == null) databaseBridgeInstance = new DatabaseBridge();

        return databaseBridgeInstance;

    }

    @Override
    public void init() {

        this.databaseConnector = new DatabaseConnector();

        this.databaseConnector.init();

    }

    @Override
    public void finish() {

        this.databaseConnector.finish();

    }

    private ProductRecord productToRecord(Product product) {

        return new ProductRecord(
                product.getId(),
                product.getTitle(),
                product.getRating(),
                product.getSalesrank(),
                product.getImage()
        );

    }

    private CustomerRecord customerToRecord(Customer customer){

        return new CustomerRecord(
                customer.getId(),
                customer.getUsername(),
                customer.getAccountNumber(),
                customer.getAddress() == null ? "" : customer.getAddress().toString()
        );

    }

    private OfferRecord offerToRecord(ProductsInStore productsInStore){

        return new OfferRecord(
                productsInStore.getId().toString(),
                productsInStore.getStore().toString(),
                productToRecord(productsInStore.getProduct()),
                productsInStore.getPrice(),
                productsInStore.getCurrency(),
                productsInStore.getAvailability()
        );
    }

    private CategoryRecord categoryToRecord(Category category){

        return new CategoryRecord(
                category.getId(),
                category.getName(),
                category.getParentCategory() == null ? "ROOT" : category.getParentCategory().getName(),
                category.getSubCategories().stream().map(Category::getName).toList()
        );
    }

    private ReviewRecord reviewToRecord(Review review){

        return new ReviewRecord(
                review.getId().toString(),
                customerToRecord(review.getCustomer()),
                productToRecord(review.getProduct()),
                review.getDescription(),
                review.getSummary(),
                review.getPoints(),
                review.getHelpful()
        );

    }

    @Override
    public ProductRecord getProduct(String productId) {

        Product queryResult = databaseConnector.getProduct(productId);

        if(queryResult == null){

            return null;

        } else {

            return productToRecord(queryResult);

        }

    }

    @Override
    public List<ProductRecord> getProducts(String pattern) {

        List<Product> queryResult = databaseConnector.getProducts(pattern);

        if(queryResult == null){

            return null;

        } else {

            return queryResult.stream().map(this::productToRecord).toList();

        }

    }

    @Override
    public CategoryRecord getCategoryTree() {

        return categoryToRecord(this.databaseConnector.getCategoryTree());

    }

    @Override
    public List<ProductRecord> getProductsByCategoryPath(String path) {

        final List<String> listPath = new ArrayList<String>(Arrays.asList(path.split("\\|")));

        final List<String> trimmedListPath = new ArrayList<String>(listPath.stream().map(String::trim).toList());

        List<Product> queryResult = databaseConnector.getProductsByCategoryPath(trimmedListPath);

        if(queryResult == null){

            return null;

        } else {

            return queryResult.stream().map(this::productToRecord).toList();

        }
    }

    @Override
    public List<ProductRecord> getTopProducts(int k) {

        List<Product> queryResult = databaseConnector.getTopProducts(k);

        if(queryResult == null){

            return null;

        } else {

            return queryResult.stream().map(this::productToRecord).toList();

        }
    }

    @Override
    public List<ProductRecord> getSimilarCheaperProduct(String productId) {

        List<Product> queryResult = databaseConnector.getSimilarCheaperProduct(productId);

        if(queryResult == null){

            return null;

        } else {

            return queryResult.stream().map(this::productToRecord).toList();

        }
    }

    @Override
    public List<ReviewRecord> getReviews(String productId) {
       List<Review> queryResult = databaseConnector.getReviews(productId);
       if(queryResult == null){
           return null;
       }else {
           return queryResult.stream().map(this::reviewToRecord).toList();
       }
    }

    @Override
    public ReviewRecord addNewReview(String username, String productId, String description, int points) {

        return reviewToRecord(this.databaseConnector.addNewReview(username, productId, description, points));

    }

    @Override
    public List<CustomerRecord> getTrolls(float threshold) {

        List<Customer> queryResult = databaseConnector.getTrolls(threshold);

        if(queryResult == null){

            return null;

        } else {

            return queryResult.stream().map(this::customerToRecord).toList();

        }
    }

    @Override
    public List<OfferRecord> getOffers(String productId) {

        List<ProductsInStore> queryResult = databaseConnector.getOffers(productId);

        if(queryResult == null){

            return null;

        } else {

            return queryResult.stream().map(this::offerToRecord).toList();

        }
    }

}
