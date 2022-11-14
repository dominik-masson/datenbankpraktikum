package gruppe20.dbpraktikum;

import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Elements;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class StoreImporter {

    private static final StoreImporter instance = new StoreImporter();
    private static final Logger logger = LogManager.getLogger();
    private static final DateFormat DateFormatter = new SimpleDateFormat("yyyy-MM-dd");
    private static String log_asin = "";

    private Connection connection;

    public static StoreImporter getInstance() {

        return instance;

    }

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public int getOrCreateAddress(String street, int number, int zip) {

        try {
            PreparedStatement getExistingAddress = connection.prepareStatement("SELECT uid FROM address WHERE street = ? AND number = ? AND zip = ?");

            getExistingAddress.setString(1, street);
            getExistingAddress.setInt(2, number);
            getExistingAddress.setInt(3, zip);

            ResultSet result = getExistingAddress.executeQuery();

            if (result.next()) {

                return result.getInt(1);

            } else {

                PreparedStatement createNewAddress = connection.prepareStatement("INSERT INTO address(street, number, zip) VALUES (?, ?, ?)", PreparedStatement.RETURN_GENERATED_KEYS);

                createNewAddress.setString(1, street);
                createNewAddress.setInt(2, number);
                createNewAddress.setInt(3, zip);

                createNewAddress.executeUpdate();

                ResultSet result2 = createNewAddress.getGeneratedKeys();
                result2.next();
                return result2.getInt(1);

            }
        } catch (SQLException e) {

            logger.error("failed to create or fetch address: %s".formatted(e.getMessage()));

            return 0;

        }


    }

    private int createStore(String name, int addressUid) {

        try {

            PreparedStatement createStore = connection.prepareStatement("INSERT INTO store(name, address) VALUES (?, ?)", PreparedStatement.RETURN_GENERATED_KEYS);

            createStore.setString(1, name);
            createStore.setInt(2, addressUid);

            createStore.executeUpdate();

            ResultSet result = createStore.getGeneratedKeys();
            result.next();
            return result.getInt(1);


        } catch (SQLException e) {

            logger.error("failed to create store: %s".formatted(e.getMessage()));
            return 0;

        }

    }


    public Optional<String> getOptionalStringAttribute(Element item, String name) {

        Optional<String> result;

        try {

            result = Optional.of(item.getAttributeValue(name));

            if (result.get() == "") {

                result = Optional.empty();

            }

        } catch (NullPointerException e) {

            result = Optional.empty();
            logger.warn("No %s found for %s".formatted(name, item.getAttributeValue("asin")));

        }

        return result;

    }

    public Optional<Integer> getOptionalIntegerAttribute(Element item, String name) {

        Optional<Integer> result;

        try {

            result = Optional.of(Integer.parseInt(item.getAttributeValue(name)));

        } catch (NumberFormatException e) {

            result = Optional.empty();
            logger.warn("No %s found for %s".formatted(name, item.getAttributeValue("asin")));

        }

        return result;

    }

    public Optional<String> getOptionalStringValue(Element item) {

        Optional<String> result;

        try {

            result = Optional.of(item.getValue());

            if (result.get() == "") {

                result = Optional.empty();

            }

        } catch (NullPointerException e) {

            result = Optional.empty();
            logger.warn("No %s found for %s".formatted(item.getLocalName(), log_asin));

        }

        return result;

    }

    public Optional<Integer> getOptionalIntegerValue(Element item) {

        Optional<Integer> result;

        try {

            result = Optional.of(Integer.parseInt(item.getValue()));

        } catch (NumberFormatException e) {

            result = Optional.empty();
            logger.warn("No %s found for %s".formatted(item.getLocalName(), log_asin));

        }

        return result;

    }

    private void insertProduct(String asin, String title, Optional<Integer> salesrank, Optional<String> image) throws SQLException {

        PreparedStatement insertProduct = connection.prepareStatement("INSERT INTO product(asin, title, salesrank, image) VALUES (?, ?, ?, ?)");

        insertProduct.setString(1, asin);
        insertProduct.setString(2, title);

        if (salesrank.isPresent()) insertProduct.setInt(3, salesrank.get());
        else insertProduct.setNull(3, Types.INTEGER);

        if (image.isPresent()) insertProduct.setString(4, image.get());
        else insertProduct.setNull(4, Types.VARCHAR);

        insertProduct.executeUpdate();
    }

    private void insertProductInStore(Element item, String asin, int shopId) {

        Element price = item.getFirstChildElement("price");

        float mult;
        int value;

        Optional<Float> product_price;

        boolean available = true;

        try {

            mult = Float.parseFloat(price.getAttributeValue("mult"));
            value = Integer.parseInt(price.getValue());

            product_price = Optional.of(mult * value);

        } catch (NumberFormatException | NullPointerException e) {

            available = false;
            product_price = Optional.empty();
            //logger.warn("Could not parse price for %s".formatted(asin));

        }

        Optional<String> currency = getOptionalStringAttribute(price, "currency");
        Optional<String> condition = getOptionalStringAttribute(price, "state");

        try {
            PreparedStatement insertProductInStore = connection.prepareStatement("INSERT INTO products_in_store(store, product, price, currency, availability, condition) VALUES (?, ?, ?, ?, ?, ?)");

            insertProductInStore.setInt(1, shopId);
            insertProductInStore.setString(2, asin);

            if (product_price.isPresent()) insertProductInStore.setFloat(3, product_price.get());
            else insertProductInStore.setNull(3, Types.FLOAT);

            if (currency.isPresent()) insertProductInStore.setString(4, currency.get());
            else insertProductInStore.setNull(4, Types.VARCHAR);

            insertProductInStore.setBoolean(5, available);

            if (condition.isPresent()) insertProductInStore.setString(6, condition.get());
            else insertProductInStore.setNull(6, Types.VARCHAR);

            insertProductInStore.executeUpdate();

        } catch (SQLException e) {

            logger.error("failed to insert product %s to store with id %s: %s".formatted(asin, shopId, e.getMessage()));
        }


    }

    private void parseItem(Element item, int shopId) {

        String asin = item.getAttributeValue("asin");
        String title = "";

        try {
            title = item.getFirstChildElement("title").getValue();

            if (title == null) {

                logger.error("No tile found for %s".formatted(asin));
                return;
            }

        } catch (NullPointerException e) {

            logger.error("No tile found for %s".formatted(asin));
            return;
        }

        if (asin == null) {

            logger.error("No asin found for %s".formatted(title));
            return;

        }

        log_asin = asin;

        Optional<Integer> salesrank = getOptionalIntegerAttribute(item, "salesrank");
        Optional<String> image;

        if (item.getFirstChildElement("details") != null) {

            image = getOptionalStringAttribute(item.getFirstChildElement("details"), "img");

        } else {

            image = getOptionalStringAttribute(item, "picture");

        }

        try {

            insertProduct(asin, title, salesrank, image);
            insertProductInStore(item, asin, shopId);

        } catch (SQLException e) {

            logger.error("failed to insert product %s: %s".formatted(asin, e.getMessage()));

            insertProductInStore(item, asin, shopId);
            return;

        }

        String group = item.getAttributeValue("pgroup");

        if (group.equals("DVD")) {

            parseDvd(item, asin);

        } else if (group.equals("Music")) {

            parseMusic(item, asin);

        } else if (group.equals("Book")) {

            parseBook(item, asin);

        } else {

            logger.error("Could not assign group %s for %s".formatted(group, asin));

        }

    }

    private void parseSimilars(Element item) {

        String asin = item.getAttributeValue("asin");

        if (asin == null) return;

        for (Element similar : item.getFirstChildElement("similars").getChildElements()) {

            String asin2 = "";

            if (similar.getFirstChildElement("asin") != null) {

                asin2 = similar.getFirstChildElement("asin").getValue();

            } else if (similar.getAttribute("asin") != null) {

                asin2 = similar.getAttributeValue("asin");

            }

            if (!asin2.equals("") && !asin.equals("")) {

                try {

                    PreparedStatement getExistingPair = connection.prepareStatement("SELECT * FROM similar_product WHERE product1 = ? AND product2 = ?");
                    PreparedStatement insertPair = connection.prepareStatement("INSERT INTO similar_product(product1, product2) VALUES (?, ?)");

                    getExistingPair.setString(1, asin);
                    getExistingPair.setString(2, asin2);

                    ResultSet result = getExistingPair.executeQuery();

                    if (!result.next()) {

                        insertPair.setString(1, asin);
                        insertPair.setString(2, asin2);

                        insertPair.executeUpdate();

                    }

                    getExistingPair.setString(1, asin2);
                    getExistingPair.setString(2, asin);


                    ResultSet result2 = getExistingPair.executeQuery();

                    if (!result2.next()) {

                        insertPair.setString(1, asin2);
                        insertPair.setString(2, asin);

                        insertPair.executeUpdate();

                    }


                } catch (SQLException e) {

                    logger.error("failed to insert similar products %s and %s: %s".formatted(asin, asin2, e.getMessage()));

                }

            }

        }

    }

    private int getOrCreatePerson(String name) {
        try {
            PreparedStatement getExistingPerson = connection.prepareStatement("SELECT uid FROM person WHERE name = ?");

            getExistingPerson.setString(1, name);

            ResultSet result = getExistingPerson.executeQuery();

            if (result.next()) {

                return result.getInt(1);

            } else {

                PreparedStatement createNewPerson = connection.prepareStatement("INSERT INTO person(name) VALUES (?)", PreparedStatement.RETURN_GENERATED_KEYS);

                createNewPerson.setString(1, name);

                createNewPerson.executeUpdate();

                ResultSet result2 = createNewPerson.getGeneratedKeys();
                result2.next();
                return result2.getInt(1);

            }
        } catch (SQLException e) {

            logger.error("failed to create or fetch person: %s".formatted(e.getMessage()));

            return 0;

        }
    }

    private int getOrCreateCompany(String name) {
        try {
            PreparedStatement getExistingCompany = connection.prepareStatement("SELECT uid FROM company WHERE name = ?");

            getExistingCompany.setString(1, name);

            ResultSet result = getExistingCompany.executeQuery();

            if (result.next()) {

                return result.getInt(1);

            } else {

                PreparedStatement createNewCompany = connection.prepareStatement("INSERT INTO company(name) VALUES (?)", PreparedStatement.RETURN_GENERATED_KEYS);

                createNewCompany.setString(1, name);

                createNewCompany.executeUpdate();

                ResultSet result2 = createNewCompany.getGeneratedKeys();
                result2.next();
                return result2.getInt(1);

            }
        } catch (SQLException e) {

            logger.error("failed to create or fetch company: %s".formatted(e.getMessage()));

            return 0;

        }
    }

    private void parseAssociatedCompanies(Element item, String role, String asin) {

        for (Element company : item.getChildElements()) {

            int uid;

            if (!company.getValue().isEmpty()) {
                uid = getOrCreateCompany(company.getValue());
            } else if (company.getAttributeValue("name") != null) {
                uid = getOrCreateCompany(company.getAttributeValue("name"));
            } else {
                return;
            }

            try {
                PreparedStatement createLink = connection.prepareStatement("INSERT INTO associated_company(company, product, role) VALUES (?, ?, ?)");

                createLink.setInt(1, uid);
                createLink.setString(2, asin);
                createLink.setString(3, role);

                createLink.executeUpdate();

            } catch (SQLException e) {

                logger.error("failed to link %s to %s: %s".formatted(role, asin, e.getMessage()));
                return;

            }

        }

    }

    private void parseAssociatedPersons(Element item, String title, String asin) {

        for (Element person : item.getChildElements()) {

            int uid;

            if (!person.getValue().isEmpty()) {
                uid = getOrCreatePerson(person.getValue());
            } else if (person.getAttributeValue("name") != null) {
                uid = getOrCreatePerson(person.getAttributeValue("name"));
            } else {
                return;
            }

            try {
                PreparedStatement createLink = connection.prepareStatement("INSERT INTO associated_person(person, product, title) VALUES (?, ?, ?)");

                createLink.setInt(1, uid);
                createLink.setString(2, asin);
                createLink.setString(3, title);

                createLink.executeUpdate();

            } catch (SQLException e) {

                logger.error("failed to link %s to %s: %s".formatted(title, asin, e.getMessage()));
                return;

            }

        }
    }

    private void parseBook(Element item, String asin) {

        Element book = item.getFirstChildElement("bookspec");

        String isbn;

        try {


            isbn = book.getFirstChildElement("isbn").getAttributeValue("val");

            if (isbn == "") {

                logger.error("Could not find isbn for %s".formatted(asin));
                return;

            }

        } catch (NullPointerException e) {

            logger.error("Could not find isbn for %s".formatted(asin));
            return;
        }

        Optional<Integer> pages = getOptionalIntegerValue(book.getFirstChildElement("pages"));

        Optional<String> rawDate = getOptionalStringAttribute(book.getFirstChildElement("publication"), "date");

        try {

            PreparedStatement insertBook = connection.prepareStatement("INSERT INTO book(asin, pages, release_date, isbn) VALUES (?, ?, ?, ?)");


            insertBook.setString(1, asin);
            insertBook.setString(4, isbn);

            if (pages.isPresent()) insertBook.setInt(2, pages.get());
            else insertBook.setNull(2, Types.INTEGER);

            if (rawDate.isPresent())
                insertBook.setDate(3, new java.sql.Date(DateFormatter.parse(rawDate.get()).getTime()));
            else insertBook.setNull(3, Types.DATE);

            insertBook.executeUpdate();

        } catch (SQLException | ParseException e) {

            logger.error("failed to insert book %s: %s".formatted(asin, e.getMessage()));
            return;
        }

        parseAssociatedCompanies(item.getFirstChildElement("publishers"), "publisher", asin);
        parseAssociatedPersons(item.getFirstChildElement("authors"), "author", asin);

    }

    private void parseMusic(Element item, String asin) {

        Element cd = item.getFirstChildElement("musicspec");

        String ean;

        try {

            if (item.getFirstChildElement("ean") != null) {
                ean = item.getFirstChildElement("ean").getValue();
            } else {
                ean = item.getAttributeValue("ean");
            }

            if (ean == "") {

                logger.error("Could not find ean for %s".formatted(asin));
                return;

            }

        } catch (NullPointerException e) {

            logger.error("Could not find ean for %s".formatted(asin));
            return;
        }

        Optional<String> rawDate = getOptionalStringValue(cd.getFirstChildElement("releasedate"));

        List<String> tracks = new ArrayList<>();

        item.getFirstChildElement("tracks").getChildElements().forEach(track -> tracks.add(track.getValue()));

        try {

            PreparedStatement insertCd = connection.prepareStatement("INSERT INTO cd(asin, ean, release_date, tracks) VALUES (?, ?, ?, ?)");

            insertCd.setString(1, asin);
            insertCd.setString(2, ean);

            if (rawDate.isPresent())
                insertCd.setDate(3, new java.sql.Date(DateFormatter.parse(rawDate.get()).getTime()));
            else insertCd.setNull(3, Types.DATE);

            final String[] tracksArray = tracks.toArray(new String[tracks.size()]);

            final java.sql.Array tracksSqlArray = connection.createArrayOf("TEXT", tracksArray);

            insertCd.setArray(4, tracksSqlArray);

            insertCd.executeUpdate();

        } catch (SQLException | ParseException e) {

            logger.error("failed to insert book %s: %s".formatted(asin, e.getMessage()));
            return;
        }

        parseAssociatedCompanies(item.getFirstChildElement("labels"), "label", asin);

        parseAssociatedPersons(item.getFirstChildElement("artists"), "artist", asin);


    }

    private void parseDvd(Element item, String asin) {

        Element dvd = item.getFirstChildElement("dvdspec");

        String ean;

        try {

            if (item.getFirstChildElement("ean") != null) {
                ean = item.getFirstChildElement("ean").getValue();
            } else {
                ean = item.getAttributeValue("ean");
            }

            if (ean == "") {

                logger.error("Could not find ean for %s".formatted(asin));
                return;

            }

        } catch (NullPointerException e) {

            logger.error("Could not find ean for %s".formatted(asin));
            return;
        }

        Optional<Integer> runtime = getOptionalIntegerValue(dvd.getFirstChildElement("runningtime"));

        Optional<Integer> region_code = getOptionalIntegerValue(dvd.getFirstChildElement("regioncode"));
        ;

        Optional<String> format = getOptionalStringValue(dvd.getFirstChildElement("format"));
        ;

        Optional<String> rawDate = getOptionalStringValue(dvd.getFirstChildElement("releasedate"));

        try {

            PreparedStatement insertDvd = connection.prepareStatement("INSERT INTO dvd(asin, ean, format, runtime, region_code, release_date) VALUES (?, ?, ?, ?, ?, ?)");


            insertDvd.setString(1, asin);
            insertDvd.setString(2, ean);

            if (format.isPresent()) insertDvd.setString(3, format.get());
            else insertDvd.setNull(3, Types.VARCHAR);

            if (runtime.isPresent()) insertDvd.setInt(4, runtime.get());
            else insertDvd.setNull(4, Types.INTEGER);

            if (region_code.isPresent()) insertDvd.setInt(5, region_code.get());
            else insertDvd.setNull(5, Types.INTEGER);

            if (rawDate.isPresent())
                insertDvd.setDate(6, new java.sql.Date(DateFormatter.parse(rawDate.get()).getTime()));
            else insertDvd.setNull(6, Types.DATE);

            insertDvd.executeUpdate();

        } catch (SQLException | ParseException e) {

            logger.error("failed to insert book %s: %s".formatted(asin, e.getMessage()));
            return;
        }

        parseAssociatedCompanies(item.getFirstChildElement("studios"), "studio", asin);

        parseAssociatedPersons(item.getFirstChildElement("actors"), "actor", asin);
        parseAssociatedPersons(item.getFirstChildElement("creators"), "creator", asin);
        parseAssociatedPersons(item.getFirstChildElement("directors"), "director", asin);

    }

    public void parseItemsFromXmlDocument(Document document) {

        Element root = document.getRootElement();

        int addressUid = getOrCreateAddress(root.getAttributeValue("street"), 0, Integer.parseInt(root.getAttributeValue("zip")));

        int shopUid = createStore(root.getAttributeValue("name"), addressUid);

        Elements children = root.getChildElements("item");

        children.forEach(item -> parseItem(item, shopUid));
        //children.forEach(this::parseSimilars);

    }

    public void parseSimilarItemsFromXmlDocument(Document document) {

        Element root = document.getRootElement();
        Elements children = root.getChildElements("item");

        children.forEach(this::parseSimilars);

    }

}
