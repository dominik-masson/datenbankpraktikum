package db.gruppe20.abgabe3.backend;

import db.gruppe20.abgabe3.backend.entity.*;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.*;
import org.hibernate.service.ServiceRegistry;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class DatabaseConnector implements IDatabaseConnection {

    private SessionFactory sessionFactory;

    @Override
    public void init() {
        try {
            Configuration configuration = new Configuration();

            ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder().applySettings(configuration.getProperties()).build();
            configuration.addAnnotatedClass(Cd.class)
                    .addAnnotatedClass(Company.class)
                    .addAnnotatedClass(Customer.class)
                    .addAnnotatedClass(Store.class)
                    .addAnnotatedClass(Address.class)
                    .addAnnotatedClass(Dvd.class)
                    .addAnnotatedClass(Book.class)
                    .addAnnotatedClass(Person.class)
                    .addAnnotatedClass(Product.class)
                    .addAnnotatedClass(Review.class)
                    .addAnnotatedClass(ReviewId.class)
                    .addAnnotatedClass(Product.class)
                    .addAnnotatedClass(AssociatedPerson.class)
                    .addAnnotatedClass(AssociatedPersonId.class)
                    .addAnnotatedClass(ProductsInStore.class)
                    .addAnnotatedClass(ProductsInStoreId.class)
                    .addAnnotatedClass(Category.class)
                    .addAnnotatedClass(IsInCategory.class)
                    .addAnnotatedClass(IsInCategoryId.class)
                    .addAnnotatedClass(Sale.class)
                    .addAnnotatedClass(SimilarProduct.class)
                    .addAnnotatedClass(SimilarProductId.class)
                    .addAnnotatedClass(AssociatedCompany.class)
                    .addAnnotatedClass(AssociatedCompanyId.class)
                    .addAnnotatedClass(LowestPrice.class);


            sessionFactory = configuration.buildSessionFactory(serviceRegistry);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public void finish() {
        if (sessionFactory != null) {
            sessionFactory.close();
        } else {
            System.out.println("Connection was not active");
        }
    }

    @Override
    public Product getProduct(String productId) {
        Session session = sessionFactory.openSession();

        Product result = session.get(Product.class, productId);

        session.close();
        return result;  //.toString();
    }

    @Override
    public List<Product> getProducts(String pattern) {
        Session session = sessionFactory.openSession();

        if (pattern == null) {
            pattern = "%";
        }

        List<Product> result = session
                .createQuery("FROM Product WHERE title LIKE :pattern", Product.class)
                .setParameter("pattern", "%" + pattern + "%")
                .list();

        session.close();
        return result; //.toString();

    }

    @Override
    public Category getCategoryTree() {
        Session session = sessionFactory.openSession();

        Category result = getCategoryTreeRoots(session);
        result.initializeSubCategories();

        session.close();
        return result; //.toString();
    }

    @Override
    public List<Product> getProductsByCategoryPath(List<String> path) {
        if (path.isEmpty()) {
            return new ArrayList<>();
        }

        Session session = sessionFactory.openSession();

        Category category = followPath(path, getCategoryTreeRoots(session));
        List<Product> result;

        if (category == null) {
            return new ArrayList<>();
        } else {
            result = category.getProductList();
        }

        session.close();
        return result;
    }

    private Category followPath(List<String> path, Category parent) {
        if (path.isEmpty()) {
            return parent;
        }

        for (Category child : parent.getSubCategories()) {
            if (path.get(0).equals(child.getName())) {
                path.remove(0);
                return followPath(path, child);
            }
        }

        return null;
    }

    private Category getCategoryTreeRoots(Session session) {
        List<Category> categories = session
                .createQuery("FROM Category WHERE parentCategory IS NULL", Category.class)
                .list();

        Category root = new Category();
        root.setName("root");
        root.setSubCategories(categories);

        return root;
    }

    @Override
    public List<Product> getTopProducts(int k) {
        Session session = sessionFactory.openSession();

        List<Product> result = session
                .createQuery("FROM Product product ORDER BY rating DESC NULLS LAST", Product.class)
                .setMaxResults(k)
                .list();

        session.close();
        return result; //.toString();
    }
        @Override
    public List<Product> getSimilarCheaperProduct(String productId) {
        Session session = sessionFactory.openSession();

        List<Product> result = session
                .createQuery("SELECT p2 FROM Product p1, Product p2 " +
                        "WHERE p1.id = :productId " +
                        "AND p2 MEMBER OF p1.similarProduct " +
                        "AND p2.lowestPrice < p1.lowestPrice AND p2.lowestPrice IS NOT NULL", Product.class)
                .setParameter("productId", productId)
                .list();

        session.close();
        return result; //.toString();
    }

    @Override
    public List<Review> getReviews(String productId) {
        Session session = sessionFactory.openSession();

        List<Review> result = session
                .createQuery("FROM Review review WHERE review.id.product = :productId", Review.class)
                .setParameter("productId", productId)
                .list();

        session.close();
        return result; //.toString();
    }

    @Override
    public Review addNewReview(String customerName, String productId, String description, int points) {

        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();

        Customer customer = (Customer) session
                .createQuery("FROM Customer WHERE username = :customerName")
                .setParameter("customerName", customerName)
                .uniqueResult();

        if (customer == null) {
            customer = new Customer();
            customer.setUsername(customerName);
            session.persist(customer);
        }

        Product product = session.get(Product.class, productId);

        ReviewId reviewId = new ReviewId();

        reviewId.setCustomer(customer.getId());
        reviewId.setProduct(productId);
        reviewId.setTimestamp(Instant.now());

        Review review = new Review();

        review.setId(reviewId);
        review.setCustomer(customer);
        review.setProduct(product);
        review.setDescription(description);
        review.setPoints(points);
        review.setSummary(description);

        session.persist(review);

        transaction.commit();
        session.close();

        return review;

    }

    @Override
    public List<Customer> getTrolls(double threshold) {
        Session session = sessionFactory.openSession();

        List<Customer> result = session
                .createQuery("SELECT customer FROM Customer customer " +
                        "RIGHT JOIN Review  review " +
                        "ON customer.id = review.id.customer " +
                        "GROUP BY customer.id, review.customer, review.product, review.id.timestamp " +
                        "HAVING AVG(review.points) < :threshold", Customer.class)
                .setParameter("threshold", threshold)
                .getResultList();

        session.close();
        return result;
    }

    @Override
    public List<ProductsInStore> getOffers(String productId) {
        Session session = sessionFactory.openSession();

        Optional<Product> product = session.byId(Product.class).loadOptional(productId);

        List<ProductsInStore> result;

        if (product.isEmpty()) {
            result = new ArrayList<>();
        } else {
            result = product.get().getOffers();
            result.removeIf(x -> !x.getAvailability());

        }

        session.close();
        return result; //.toString();
    }

}
