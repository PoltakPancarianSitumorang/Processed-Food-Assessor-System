
public class Nutrient {

    private String name;
    private double amount;

    // Create a nutrient
    public Nutrient(String name, double amount) {
        this.name = name;
        this.amount = amount;
    }

    // Return a string representation
    @Override
    public String toString() {
        return name + ": " + amount;
    }

    //
    // Setter and getter methods
    //
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

}
