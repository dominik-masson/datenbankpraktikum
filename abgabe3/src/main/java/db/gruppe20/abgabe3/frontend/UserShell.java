package db.gruppe20.abgabe3.frontend;

import db.gruppe20.abgabe3.bridge.DatabaseBridge;
import db.gruppe20.abgabe3.frontend.entity.*;
import org.springframework.shell.Availability;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellMethodAvailability;
import org.springframework.shell.standard.ShellOption;

import javax.validation.constraints.Size;
import java.util.List;

@ShellComponent
public class UserShell implements IDatabaseQuery {

    private DatabaseBridge databaseBridge;
    private boolean connected;

    @Override
    @ShellMethod(value = "Initialize database connection. Must be executed first.", group = "Database Connection")
    public void init() {
        connected = true;
        this.databaseBridge = DatabaseBridge.getInstance();

        this.databaseBridge.init();

    }

    @Override
    @ShellMethod(value = "Terminate the database connection.", group = "Database Connection")
    @ShellMethodAvailability("availabilityCheck")
    public void finish() {
        connected = false;
        this.databaseBridge.finish();

    }

    @Override
    @ShellMethod(value = "Get all information about a specific product with an attached ASIN (Product ID).", group = "Product Commands")
    @ShellMethodAvailability("availabilityCheck")
    public ProductRecord getProduct(String productId) {
        return this.databaseBridge.getProduct(productId);

    }

    @Override
    @ShellMethod(value = "Get all products that match a given pattern.", group = "Product Commands")
    @ShellMethodAvailability("availabilityCheck")
    public List<ProductRecord> getProducts(@ShellOption(defaultValue = "") String pattern) {

        return this.databaseBridge.getProducts(pattern);
    }

    @Override
    @ShellMethod(value = "get a list of all root nodes of a category tree.", group = "Category Commands")
    @ShellMethodAvailability("availabilityCheck")
    public CategoryRecord getCategoryTree() {

        return this.databaseBridge.getCategoryTree();

    }

    @Override
    @ShellMethod(value = "search for products that are in a category. the input must be a valid path.", group = "Category Commands")
    @ShellMethodAvailability("availabilityCheck")
    public List<ProductRecord> getProductsByCategoryPath(String path) {

        return this.databaseBridge.getProductsByCategoryPath(path);

    }

    @Override
    @ShellMethod(value = "get all the products that are below a given value.", group = "Product Commands")
    @ShellMethodAvailability("availabilityCheck")
    public List<ProductRecord> getTopProducts(int k) {

        return this.databaseBridge.getTopProducts(k);

    }

    @Override
    @ShellMethod(value = "get a list of products that are similar and cheaper to a given product. Please enter the ASIN of the product.", group = "Product Commands")
    @ShellMethodAvailability("availabilityCheck")
    public List<ProductRecord> getSimilarCheaperProduct(String productId) {

        return this.databaseBridge.getSimilarCheaperProduct(productId);

    }

    @ShellMethod(value = "get all reviews for one product.", group = "Review Commands")
    @ShellMethodAvailability("availabilityCheck")
    public List<ReviewRecord> getReviews(String productId){
        return  this.databaseBridge.getReviews(productId);
    }

    @Override
    @ShellMethod(value = "create a new review. Please include your username, the product ASIN, a description and the points.", group = "Review Commands")
    @ShellMethodAvailability("availabilityCheck")
    public ReviewRecord addNewReview(String username, String productId, String description, int points) {

        return this.databaseBridge.addNewReview(username, productId, description, points);

    }

    @Override
    @ShellMethod(value = "returns all users whose rating is below a certain value.", group = "Review Commands")
    @ShellMethodAvailability("availabilityCheck")
    public List<CustomerRecord> getTrolls(float threshold) {
        return this.databaseBridge.getTrolls(threshold);

    }

    @Override
    @ShellMethod(value = "get all available quotes for a product.", group = "Product Commands")
    @ShellMethodAvailability("availabilityCheck")
    public List<OfferRecord> getOffers(String productId) {

        return this.databaseBridge.getOffers(productId);
    }

    @ShellMethodAvailability({"getOffers","getTrolls","addNewReview","getSimilarCheaperProduct","getProduct", "getProducts","getCategoryTree","getProductsByCategoryPath","getTopProducts"})
    public Availability availabilityCheck() {
        return connected
                ? Availability.available()
                : Availability.unavailable("Database connection must be initialized first. Use the command \"init\" for this.");
    }
}
