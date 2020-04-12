
public class Item {

    private String name;
    private String category;
    
    // Create a new menu item
    public Item(String name, String category) {
        this.name = name;
        this.category = category;
    }

    //
    // Setter and getter methods
    //
    public void setCategory(String category) {
        this.category = category;
    }

    public String getCategory() {
        return category;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    // Get string representation
    @Override
    public String toString() {
        String string = "";
        string += String.format("%-10s", category);
        string += String.format("%-60s", name);
        return string;
    }
}
