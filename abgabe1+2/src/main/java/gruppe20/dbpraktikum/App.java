package gruppe20.dbpraktikum;

import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.ParsingException;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

@SpringBootApplication
public class App {

	public static final Logger logger = LogManager.getLogger();

	public static final int PORT = 49153; //49153;
	public static final String ADDRESS = "localhost"; //"localhost";

	public static void main(String[] args) {

		try {

			logger.info("Connecting to database");

			Properties props = new Properties();
			props.setProperty("user", "mediastore");
			props.setProperty("password", "1234");

			Connection connection = DriverManager.getConnection("jdbc:postgresql://%s:%s/mediastore".formatted(ADDRESS, PORT), props);

			logger.info("Established connection");

			StoreImporter storeImporter = StoreImporter.getInstance();

			storeImporter.setConnection(connection);

			Builder parser = new Builder();
			Document leipzig = parser.build("data/leipzig_transformed.xml");
			Document dresden = parser.build("data/dresden.xml");


			logger.info("--- Inserting Leipzig Store ---");
			storeImporter.parseItemsFromXmlDocument(leipzig);

			logger.info("--- Inserting Dresden Store ---");
			storeImporter.parseItemsFromXmlDocument(dresden);

			logger.info("--- Inserting Leipzig Store Similar Products ---");
			storeImporter.parseSimilarItemsFromXmlDocument(leipzig);

			logger.info("--- Inserting Dresden Store Similar Products ---");
			storeImporter.parseSimilarItemsFromXmlDocument(dresden);
			logger.info("--- Reading reviews ---");
			ReviewsImporter reviewsImporter = ReviewsImporter.getInstance();
			reviewsImporter.setConnection(connection);

			FileReader csvFileReader = new FileReader("data/reviews.csv");

			CSVFormat csvFormat = CSVFormat.DEFAULT.withHeader("product","rating", "helpful","reviewdate","user","summary","content").withFirstRecordAsHeader();

			CSVParser csvParser = new CSVParser(csvFileReader, csvFormat);
			reviewsImporter.readCsvDocument(csvParser);
			logger.info("Reviews was read successfully");


			logger.info("--- Reading categories ---");

			CategoriesImporter categoriesImporter = CategoriesImporter.getInstance();
			categoriesImporter.setConnection(connection);

			Document categories = parser.build("data/categories.xml");

			categoriesImporter.readCategoriesDocument(categories);

		} catch (SQLException e) {

			logger.error("Unable to connect to database %s".formatted(e.getMessage()));

		} catch (ParsingException e) {

			logger.error("Unable to parse xml file %s".formatted(e.getMessage()));

		} catch (IOException e) {

			logger.error("Unable to open xml file %s".formatted(e.getMessage()));
		}

	}

}
