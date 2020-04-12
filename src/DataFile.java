
import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

public class DataFile {

    private static DataFile dataFile;
    
    private ArrayList<ProcessedFood> foods = new ArrayList<>();
    private ArrayList<ProcessedFood> beverages = new ArrayList<>();

    // Initialize the data
    private DataFile(String filename) {
        loadData(filename);
    }

    // Parse the file to extract the data
    private void loadData(String filename) {
        int lineNumber = 0;

        try {
            Scanner inFile = new Scanner(new File(filename));
            String[] tokens = inFile.nextLine().split(",");
            lineNumber++;
            
            ArrayList<String> nutrientNames = new ArrayList<>();


            // Get the nutrient names
            for (int i = 5; i < tokens.length; i++) {
                nutrientNames.add(tokens[i].trim());
            }

            // Extract the food and beverages
            while (inFile.hasNextLine()) {
                String line = inFile.nextLine();
                tokens = line.split(",");
                int i = 0;

                String itemName = tokens[i++].trim();
                String category = tokens[i++].trim();
                String brand = tokens[i++].trim();
                int serveSize = Integer.parseInt(tokens[i++].trim());
                String unit = tokens[i++].trim();

                ArrayList<Nutrient> nutrients = new ArrayList<>();

                // Add the nutrients of the item
                for (; i < tokens.length; i++) {
                    String nutrientName = nutrientNames.get(i - 5);
                    double quantity = Double.parseDouble(tokens[i]);
                    nutrients.add(new Nutrient(nutrientName, quantity));
                }

                ProcessedFood item = new ProcessedFood(itemName, category, brand, serveSize, unit, nutrients);

                if (category.equalsIgnoreCase("food")) {
                    foods.add(item);
                } else if (category.equalsIgnoreCase("beverage")) {
                    beverages.add(item);
                }
            }

            inFile.close();
        } catch (Exception e) {
            System.out.println(lineNumber);
            e.printStackTrace(System.out);
            System.exit(0);
        }
    }

    //
    // Getter methods
    //
    public ArrayList<ProcessedFood> getFoods() {
        return foods;
    }

    public ArrayList<ProcessedFood> getBeverages() {
        return beverages;
    }
    
    public ArrayList<ProcessedFood> getFoodsAndBeverages() {
        ArrayList<ProcessedFood> foodsAndBeverages = new ArrayList<>();
        foodsAndBeverages.addAll(foods);
        foodsAndBeverages.addAll(beverages);
        
        return foodsAndBeverages;
    }

    // Get the one and only one instance of data file for the whole program
    public static DataFile getInstance() {
        if (dataFile == null) {
            dataFile = new DataFile("processedFoodData.csv");
        }

        return dataFile;
    }
}
