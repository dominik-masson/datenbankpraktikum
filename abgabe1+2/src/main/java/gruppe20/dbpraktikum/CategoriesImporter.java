package gruppe20.dbpraktikum;

import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Elements;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.Optional;

public class CategoriesImporter {

    private static final CategoriesImporter instance = new CategoriesImporter();
    public static final Logger logger = LogManager.getLogger();
    private Connection connection;

    public static CategoriesImporter getInstance()
    {
        return instance;
    }

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public Optional<Integer> setCategory(String name, Optional<Integer> main_category){
        try{
            PreparedStatement setCategory  = connection.prepareStatement("INSERT INTO category(name, parent_category) VALUES (?, ?)", PreparedStatement.RETURN_GENERATED_KEYS);
            setCategory.setString(1, name);
            if(main_category.isPresent()) {
                setCategory.setInt(2, main_category.get());
            } else{
                setCategory.setNull(2, Types.INTEGER);
            }

            setCategory.executeUpdate();

            ResultSet newCategories = setCategory.getGeneratedKeys();
            newCategories.next();

            return Optional.of(newCategories.getInt(1));

        } catch (SQLException e) {
            logger.error("Failed to create category: %s".formatted(e.getMessage()));
            return Optional.empty();
        }
    }

    public boolean setIsInCategory(String asin, int position){
        try{
            PreparedStatement isInCategory = connection.prepareStatement("INSERT INTO is_in_category(product, category) VALUES (?, ?)");
            isInCategory.setString(1, asin);
            isInCategory.setInt(2, position);
            isInCategory.executeUpdate();
            return true;
        } catch (SQLException e) {
            logger.error("Failed to save product in category: %s".formatted(e.getMessage()));
            return false;
        }


    }

    public void parseCategory(Element category, int parent_category_position){
        Elements subCategories = category.getChildElements("category");

        for(Element categories : subCategories)
        {
            Optional<Integer> cat = setCategory(categories.getChild(0).getValue().trim(), Optional.of(parent_category_position));

            if(cat.isEmpty()){
                continue;
            }

            Elements items = categories.getChildElements("item");
            for(Element item : items){
                setIsInCategory(item.getValue(), cat.get());
            }

            parseCategory(categories, cat.get());
        }
    }
    public void readCategoriesDocument(Document document){
        Element root = document.getRootElement();
        Elements categories = root.getChildElements("category");

        for(Element category : categories)
        {
            Optional<Integer> parent_category = setCategory(category.getChild(0).getValue().trim(), Optional.empty());

            if(parent_category.isEmpty()){
                continue;
            }

            Elements items = category.getChildElements("item");
            for(Element item : items){
                setIsInCategory(item.getValue(), parent_category.get());
            }

            parseCategory(category, parent_category.get());
        }
    }
}
