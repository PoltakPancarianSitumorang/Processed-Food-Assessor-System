
import java.util.ArrayList;

public class ProcessedFood extends Item {

    private int foodId;
    private String brandName;
    private int serveSize;
    private String serveUnit;
    private ArrayList<Nutrient> nutrients;

    // Default constructor
    public ProcessedFood() {
        this("", "", "", 0, "", new ArrayList<>());
    }

    // Constructor
    public ProcessedFood(String name, String category, String brandName, int serveSize, String serveUnit, ArrayList<Nutrient> nutrients) {
        super(name, category);

        this.brandName = brandName;
        this.serveSize = serveSize;
        this.serveUnit = serveUnit;
        this.nutrients = nutrients;
    }

    // Copy constructor
    public ProcessedFood(ProcessedFood other) {
        this(other.getName(), other.getCategory(), other.brandName, other.serveSize, other.serveUnit, new ArrayList<>(other.nutrients));
    }

    // Return a string representation of food
    @Override
    public String toString() {
        String string = super.toString();

        string += String.format("%-12d", serveSize);
        string += String.format("%-5s", serveUnit);
        
        for (Nutrient nutrient : nutrients) {
            string += String.format("%-" + (nutrient.getName().length() + 2) + ".2f", nutrient.getAmount());
        }

        return string;
    }

    //
    // Getter and setter methods
    //
    public int getFoodId() {
        return foodId;
    }

    public void setFoodId(int foodId) {
        this.foodId = foodId;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public int getServeSize() {
        return serveSize;
    }

    public void setServeSize(int serveSize) {
        this.serveSize = serveSize;
    }

    public String getServeUnit() {
        return serveUnit;
    }

    public void setServeUnit(String serveUnit) {
        this.serveUnit = serveUnit;
    }

    public ArrayList<Nutrient> getNutrients() {
        return nutrients;
    }

    public void setNutrients(ArrayList<Nutrient> nutrients) {
        this.nutrients = nutrients;
    }

}
