package gruppe20.dbpraktikum;

import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class ReviewsImporter {

    private static final ReviewsImporter instance = new ReviewsImporter();
    public static final Logger logger = LogManager.getLogger();
    private Connection connection;

    private final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");


    public static ReviewsImporter getInstance() {
        return instance;
    }

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public int getOrCreateCustomer(String name){

        try {
            PreparedStatement getExistingCustomer = connection.prepareStatement("SELECT uid FROM customer WHERE username = ? ");
            getExistingCustomer.setString(1,name);
            ResultSet result = getExistingCustomer.executeQuery();

            if(result.next()){
                return result.getInt(1);
            }
            else{
                PreparedStatement createNewCustomer = connection.prepareStatement("INSERT INTO customer(username) VALUES (?)", PreparedStatement.RETURN_GENERATED_KEYS);
                createNewCustomer.setString(1,name);
                createNewCustomer.executeUpdate();

                ResultSet newCustomer = createNewCustomer.getGeneratedKeys();
                newCustomer.next();

                return newCustomer.getInt(1);
            }

        } catch (SQLException e) {
            logger.error("failed to create or fetch customer: %s".formatted(e.getMessage()));
            return 0;
        }

    }

    public void readCsvDocument(CSVParser parser) {
        for (CSVRecord csvRecord : parser) {

            try {
                Date date;
                date = dateFormat.parse(csvRecord.get(3));

                PreparedStatement createReviews = connection.prepareStatement("INSERT INTO review(customer, product, description, summary, points, helpful, timestamp) VALUES (?,?,?,?,?,?,?)");

                createReviews.setString(2,csvRecord.get(0)); // CSV product
                createReviews.setInt(5,Integer.parseInt(csvRecord.get(1))); // CSV rating
                createReviews.setInt(6,Integer.parseInt(csvRecord.get(2))); // CSV helpful
                createReviews.setTimestamp(7,new Timestamp(date.getTime())); // CSV reviewdate
                createReviews.setInt(1, getOrCreateCustomer(csvRecord.get(4))); // CSV uer
                createReviews.setString(4, csvRecord.get(5)); // CSV summary
                createReviews.setString(3, csvRecord.get(6)); // CSV description
                createReviews.executeUpdate();

            } catch (SQLException e) {
                logger.error("failed to create review: %s".formatted(e.getMessage()));
            } catch (ParseException e) {
                logger.error("invalid date: %s".formatted(e.getMessage()));
            }
        }
    }
}