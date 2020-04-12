
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class ProcessedFoodGUI extends JFrame implements ActionListener {

    private JTextField userNameField = new JTextField(20);
    private JList foodList = new JList(new DefaultListModel());
    private JList beverageList = new JList(new DefaultListModel());
    private JTextArea outputField = new JTextArea();
    private JRadioButton highProteinButton = new JRadioButton("High Protein");
    private JRadioButton lowSugarButton = new JRadioButton("Low Sugar");
    private DatabaseUtility database = DatabaseUtility.getInstance();

    private JButton displayChoices = new JButton("Display Choices");
    private JButton saveSelectionsButton = new JButton("Save Selections");
    private JButton clearDisplay = new JButton("Clear Display");

    private LinkedList<ProcessedFood> foods = new LinkedList<>();
    private LinkedList<ProcessedFood> beverages = new LinkedList<>();

    // Create the main window
    public ProcessedFoodGUI() {
        setTitle("Processed Food Assessor System");
        setLayout(new BorderLayout());
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1300, 600);
        setLocationRelativeTo(null);

        initializeInputPanel();
        initializeOutputPanel();
        initializeCommandPanel();

        clear();
        displayFoodsAndBeveragesMenu();

        setCommandButtonsEnabled(false);
        userNameField.addActionListener(this);
    }

    // Disable the buttons when the user hasn't provided any username
    private void setCommandButtonsEnabled(boolean enable) {
        displayChoices.setEnabled(enable);
        saveSelectionsButton.setEnabled(enable);
        clearDisplay.setEnabled(enable);
    }

    // Create panel for button commands
    private void initializeCommandPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(1, 4));
        panel.setBorder(BorderFactory.createTitledBorder("Command Buttons"));

        JButton quit = new JButton("Quit");

        saveSelectionsButton.addActionListener(this);
        displayChoices.addActionListener(this);
        clearDisplay.addActionListener(this);
        quit.addActionListener(this);

        panel.add(displayChoices);
        panel.add(saveSelectionsButton);
        panel.add(clearDisplay);
        panel.add(quit);

        add(BorderLayout.SOUTH, panel);
    }

    // Create panel on center
    private void initializeOutputPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Processed Food and Nutrition Information"));
        panel.add(BorderLayout.CENTER, new JScrollPane(outputField));

        outputField.setFont(new Font("Courier New", Font.PLAIN, 12));
        add(BorderLayout.CENTER, panel);
    }

    // Create panel on top
    private void initializeInputPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setPreferredSize(new Dimension(getWidth(), 200));
        panel.setBorder(BorderFactory.createTitledBorder("Input"));

        JPanel userPanel = new JPanel();
        userPanel.add(new JLabel("Input User Name (and press 'enter' key)"));
        userPanel.add(userNameField);
        panel.add(userPanel);

        JPanel preferencePanel = new JPanel();
        preferencePanel.add(new JLabel("Preference"));

        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add(highProteinButton);
        buttonGroup.add(lowSugarButton);

        highProteinButton.addActionListener(this);
        lowSugarButton.addActionListener(this);

        preferencePanel.add(highProteinButton);
        preferencePanel.add(lowSugarButton);

        panel.add(preferencePanel);

        JPanel foodPanel = new JPanel();
        foodPanel.setLayout(new BoxLayout(foodPanel, BoxLayout.Y_AXIS));
        foodPanel.add(new JLabel("Food"));
        foodPanel.add(new JScrollPane(foodList));

        JPanel beveragePanel = new JPanel();
        beveragePanel.setLayout(new BoxLayout(beveragePanel, BoxLayout.Y_AXIS));
        beveragePanel.add(new JLabel("Beverage"));
        beveragePanel.add(new JScrollPane(beverageList));

        JPanel menuPanel = new JPanel(new GridLayout(1, 2));
        menuPanel.add(foodPanel);
        menuPanel.add(beveragePanel);

        panel.add(menuPanel);

        // Populate the food and beveragess        
        for (ProcessedFood food : database.getFoodAndBeverages()) {
            if (food.getCategory().equalsIgnoreCase("food")) {
                foods.add(food);
            } else {
                beverages.add(food);
            }
        }

        add(BorderLayout.NORTH, panel);
    }

    // Display the foods and beverages menu
    private void displayFoodsAndBeveragesMenu() {
        ((DefaultListModel) foodList.getModel()).clear();
        ((DefaultListModel) beverageList.getModel()).clear();

        for (ProcessedFood food : foods) {
            ((DefaultListModel) foodList.getModel()).addElement(food.getName());
        }

        for (ProcessedFood beverage : beverages) {
            ((DefaultListModel) beverageList.getModel()).addElement(beverage.getName());
        }
    }

    // Generate a report details of the food and beverage
    private String generateReportOnSelectedFoodAndBeverage(ArrayList<ProcessedFood> items) {
        // Generate the header
        String string = String.format("%-10s", "Food Type");
        string += String.format("%-60s", "Item Name");
        string += String.format("%-12s", "Serve Size");
        string += String.format("%-5s", "Unit");

        for (String nutrientName : database.getNutrientNames()) {
            string += String.format("%-" + (nutrientName.length() + 2) + "s", nutrientName);
        }

        string += "\n";

        // Generate the content
        LinkedList<String> nutrientNames = database.getNutrientNames();
        double[] totalNutrientsPerColumn = new double[nutrientNames.size()];

        for (ProcessedFood item : items) {
            for (int j = 0; j < item.getNutrients().size(); j++) {
                Nutrient nutrient = item.getNutrients().get(j);
                totalNutrientsPerColumn[j] += nutrient.getAmount();
            }

            string += item + "\n";
        }

        // Print out the totals
        string += String.format("%-87s", "Total");

        for (int i = 0; i < nutrientNames.size(); i++) {
            string += String.format("%-" + (nutrientNames.get(i).length() + 2) + ".2f", totalNutrientsPerColumn[i]);
        }

        return string;
    }

    // Return a list of all selected items from the list
    private ArrayList<ProcessedFood> getAllSelectedItems() {
        ArrayList<ProcessedFood> items = new ArrayList<>();

        for (int i : foodList.getSelectedIndices()) {
            items.add(foods.get(i));
        }

        for (int i : beverageList.getSelectedIndices()) {
            items.add(beverages.get(i));
        }

        if (highProteinButton.isSelected()) {
            NutrientComparator comparator = new NutrientComparator("protein");
            Collections.sort(items, comparator);
            Collections.reverse(items);
        } else if (lowSugarButton.isSelected()) {
            NutrientComparator comparator = new NutrientComparator("sugar");
            Collections.sort(items, comparator);
        }

        return items;
    }

    // Create a new order for the customer
    private void saveSelections() {
        String userName = userNameField.getText().trim();

        if (userName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "A user name is required.");
            return;
        }

        ArrayList<ProcessedFood> items = getAllSelectedItems();

        if (items.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Select a food and/or a beverage.");
            return;
        }

        outputField.setText("User Name: " + userName + "\n");
        outputField.append(generateReportOnSelectedFoodAndBeverage(items));

        // Save to database
        int userId = database.insertUserData(userName);

        for (ProcessedFood item : items) {
            database.insertUserFoodChoice(userId, item.getFoodId());
        }

        JOptionPane.showMessageDialog(this, "Thank you! Your selections has been saved to the database.");
        setCommandButtonsEnabled(false);
    }

    // Display the nutritional value of the selected choice
    private void displayChoices() {
        ArrayList<ProcessedFood> items = getAllSelectedItems();

        if (items.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Select a food and/or a beverage.");
            return;
        }

        outputField.setText(generateReportOnSelectedFoodAndBeverage(items));
    }

    // Clear the fields
    private void clear() {
        foodList.clearSelection();
        beverageList.clearSelection();
        userNameField.setText("");
        outputField.setText("");
        setCommandButtonsEnabled(false);
    }

    // Sort the foods and beverages by protein from highest to lowest
    private void sortMenuByProtein() {
        NutrientComparator comparator = new NutrientComparator("protein");

        Collections.sort(foods, comparator);
        Collections.sort(beverages, comparator);

        Collections.reverse(foods);
        Collections.reverse(beverages);

        displayFoodsAndBeveragesMenu();
    }

    // Sort the foods and beverags by sugar from low to high
    private void sortMenuBySugar() {
        NutrientComparator comparator = new NutrientComparator("sugar");

        Collections.sort(foods, comparator);
        Collections.sort(beverages, comparator);

        displayFoodsAndBeveragesMenu();
    }

    // Handle button events
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == userNameField) {
            if (userNameField.getText().trim().isEmpty()) {
                setCommandButtonsEnabled(false);
            } else {
                setCommandButtonsEnabled(true);

                outputField.setText("Hello " + userNameField.getText().trim() + ", welcome to Processed Food Assessor System\n"
                        + "Select your preference of 'High Protein' or 'Low Sugar'\n"
                        + "use ctrl+click to select multiple items from the displayed list\n"
                        + "use the display choices button to view details of your choices");
            }
        }
        if (e.getSource() == highProteinButton) {
            sortMenuByProtein();
        } else if (e.getSource() == lowSugarButton) {
            sortMenuBySugar();
        } else if (e.getActionCommand().equalsIgnoreCase("Quit")) {
            System.exit(0);
        } else if (e.getActionCommand().equalsIgnoreCase("Save Selections")) {
            saveSelections();
        } else if (e.getActionCommand().equalsIgnoreCase("Display Choices")) {
            displayChoices();
        } else if (e.getActionCommand().equalsIgnoreCase("Clear Display")) {
            clear();
        }
    }

    // Compare 2 processed food by their nutrient
    private class NutrientComparator implements Comparator<ProcessedFood> {

        private String nutrientName;

        // Create a comparator initializing the name of the nutrient to use for comparing
        public NutrientComparator(String nutrientName) {
            this.nutrientName = nutrientName;
        }

        // Compare 2 nutrients based on the nutrient value
        @Override
        public int compare(ProcessedFood food1, ProcessedFood food2) {
            double food1NutrientValue = -1;

            for (Nutrient nutrient : food1.getNutrients()) {
                if (nutrient.getName().contains(nutrientName)) {
                    food1NutrientValue = nutrient.getAmount();
                    break;
                }
            }

            double food2NutrientValue = -1;

            for (Nutrient nutrient : food2.getNutrients()) {
                if (nutrient.getName().contains(nutrientName)) {
                    food2NutrientValue = nutrient.getAmount();
                    break;
                }
            }

            if (food1NutrientValue > food2NutrientValue) {
                return 1;
            }

            if (food1NutrientValue < food2NutrientValue) {
                return -1;
            }

            return 0;
        }
    }

    // Start the application
    public static void main(String[] args) {
        new ProcessedFoodGUI().setVisible(true);
    }
}
