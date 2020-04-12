
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedList;

public class DatabaseUtility {

    private static DatabaseUtility dbUtility;

    private final String urlName = "jdbc:mysql://localhost";
    private final String username = "root";
    private final String password = "MMST12009";

    private Connection sqlConnection;

    // Create an instance of a database and then connect to the database
    private DatabaseUtility() {
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            sqlConnection = DriverManager.getConnection(urlName, username, password);
            createTables();
            insertFoodData();
        } catch (Exception e) {
            e.printStackTrace(System.out);
            try {
                sqlConnection.prepareStatement("USE pfsa;").executeUpdate();
            } catch (Exception ex) {
                ex.printStackTrace(System.out);
            }
        }
    }
    
    // Get the nutrient names
    public LinkedList<String> getNutrientNames() {
        try {
            LinkedList<String> nutrientNames = new LinkedList<>();
            PreparedStatement statement = sqlConnection.prepareStatement("SELECT DISTINCT name FROM Nutrient");
            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                nutrientNames.add(rs.getString(1));
            }
            
            return nutrientNames;
        } catch (Exception e) {
            e.printStackTrace(System.out);
            System.exit(0);
        }

        return null;
    }

    // Return all food and beverags
    public LinkedList<ProcessedFood> getFoodAndBeverages() {
        try {
            LinkedList<ProcessedFood> foodAndBeverages = new LinkedList<>();

            PreparedStatement statement = sqlConnection.prepareStatement("SELECT * FROM Food");
            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                int foodId = rs.getInt("foodId");

                ProcessedFood food = new ProcessedFood(
                        rs.getString("name"),
                        rs.getString("foodType"),
                        "",
                        rs.getInt("serveSize"),
                        rs.getString("serveUnit"),
                        getNutrients(foodId));
                food.setFoodId(foodId);

                foodAndBeverages.add(food);
            }

            return foodAndBeverages;
        } catch (Exception e) {
            e.printStackTrace(System.out);
            System.exit(0);
        }

        return null;
    }

    // Return the list of nutrients of a food
    private ArrayList<Nutrient> getNutrients(int foodId) throws Exception {
        ArrayList<Nutrient> nutrients = new ArrayList<>();

        PreparedStatement statement = sqlConnection.prepareStatement("SELECT * FROM Nutrient WHERE FoodId = ?");
        statement.setInt(1, foodId);

        ResultSet rs = statement.executeQuery();

        while (rs.next()) {
            Nutrient nutrient = new Nutrient(
                    rs.getString("name"),
                    rs.getDouble("value"));

            nutrients.add(nutrient);
        }

        return nutrients;
    }

    // Create the database tables
    private void createTables() throws Exception {
        sqlConnection.prepareStatement("CREATE DATABASE pfsa;").executeUpdate();
        sqlConnection.prepareStatement("USE pfsa;").executeUpdate();

        sqlConnection.prepareStatement("CREATE TABLE Food (\n"
                + "	foodId INTEGER NOT NULL AUTO_INCREMENT,\n"
                + "	foodType VARCHAR(20) NOT NULL,\n"
                + "	name VARCHAR(100) NOT NULL,\n"
                + "	serveSize INTEGER NOT NULL,\n"
                + "	serveUnit VARCHAR(10) NOT NULL,\n"
                + "	PRIMARY KEY(foodId)\n"
                + ");").executeUpdate();

        sqlConnection.prepareStatement("CREATE TABLE Nutrient (\n"
                + "	nutrientId INTEGER NOT NULL AUTO_INCREMENT,\n"
                + "	foodId INTEGER NOT NULL,\n"
                + "	name VARCHAR(100) NOT NULL,\n"
                + "	value DECIMAL(6, 2) NOT NULL,\n"
                + "	PRIMARY KEY(nutrientId),\n"
                + "	FOREIGN KEY(foodId) REFERENCES Food(foodId)"
                + ");").executeUpdate();

        sqlConnection.prepareStatement("CREATE TABLE User (\n"
                + "	userId INTEGER NOT NULL AUTO_INCREMENT,\n"
                + "	name VARCHAR(100) NOT NULL,\n"
                + "	PRIMARY KEY(userId)\n"
                + ");").executeUpdate();

        sqlConnection.prepareStatement("CREATE TABLE User_food (\n"
                + "	userId INTEGER NOT NULL,\n"
                + "	foodId INTEGER NOT NULL,\n"
                + "	PRIMARY KEY(userId, foodId),\n"
                + "	FOREIGN KEY(userId) REFERENCES User(userId),\n"
                + "	FOREIGN KEY(foodId) REFERENCES Food(foodId)\n"
                + ");").executeUpdate();
    }

    // Insert the food data from file
    private void insertFoodData() throws Exception {
        DataFile dataFile = DataFile.getInstance();

        for (ProcessedFood food : dataFile.getFoodsAndBeverages()) {
            // Insert a food
            PreparedStatement statement = sqlConnection.prepareStatement("INSERT INTO Food VALUES(DEFAULT, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, food.getCategory());
            statement.setString(2, food.getName());
            statement.setInt(3, food.getServeSize());
            statement.setString(4, food.getServeUnit());
            statement.executeUpdate();

            ResultSet rs = statement.getGeneratedKeys();
            rs.next();

            int foodId = rs.getInt(1);
            food.setFoodId(foodId);

            // Insert the nutrients of the food
            insertFoodNutrientsData(food);
        }
    }

    // Insert nutrient details for the food
    private void insertFoodNutrientsData(ProcessedFood food) throws Exception {
        for (Nutrient nutrient : food.getNutrients()) {
            PreparedStatement statement = sqlConnection.prepareStatement("INSERT INTO Nutrient VALUES (DEFAULT, ?, ?, ?)");
            statement.setInt(1, food.getFoodId());
            statement.setString(2, nutrient.getName());
            statement.setDouble(3, nutrient.getAmount());
            statement.executeUpdate();
        }
    }

    // Insert a user data, returns the unique ID given to the user
    public int insertUserData(String name) {
        try {
            PreparedStatement statement = sqlConnection.prepareStatement("INSERT INTO User VALUES(DEFAULT, ?)", Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, name);
            statement.executeUpdate();

            ResultSet rs = statement.getGeneratedKeys();
            rs.next();
            int userId = rs.getInt(1);

            return userId;
        } catch (Exception e) {
            e.printStackTrace(System.out);
            System.exit(0);
        }

        return -1;
    }

    // Insert the selected food choice of the user
    public void insertUserFoodChoice(int userId, int foodId) {
        try {
            PreparedStatement statement = sqlConnection.prepareStatement("INSERT INTO User_food VALUES(?, ?)");
            statement.setInt(1, userId);
            statement.setInt(2, foodId);
            statement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace(System.out);
            System.exit(0);
        }
    }

    // Get the database utility object to be used
    public static DatabaseUtility getInstance() {
        if (dbUtility == null) {
            dbUtility = new DatabaseUtility();
        }

        return dbUtility;
    }
}
