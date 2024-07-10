package com.example.sumanpizzaapp;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.*;

public class HelloController {

    @FXML
    private TextField customerNameField;

    @FXML
    private TextField mobileNumberField;

    @FXML
    private CheckBox xlCheckbox;

    @FXML
    private CheckBox lCheckbox;

    @FXML
    private CheckBox mCheckbox;

    @FXML
    private CheckBox sCheckbox;

    @FXML
    private TextField toppingsField;

    @FXML
    private TableView<Order> pizzaOrdersTable;

    private Connection connection;

    public HelloController() {
        // Initialize database connection
        String url = "jdbc:mysql://localhost:3306/pizzaorders";
        String username = "root";
        String password = "";

        try {
            connection = DriverManager.getConnection(url, username, password);
            System.out.println("Database connection successful!");
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle connection error
        }
    }

    @FXML
    private void initialize() {
        // Initialize table columns with cell value factories
        TableColumn<Order, String> customerNameCol = new TableColumn<>("Customer Name");
        customerNameCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCustomerName()));

        TableColumn<Order, String> mobileCol = new TableColumn<>("Mobile");
        mobileCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getMobile()));

        TableColumn<Order, String> pizzaSizeCol = new TableColumn<>("Pizza Size");
        pizzaSizeCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPizzaSize()));

        TableColumn<Order, Integer> toppingsCol = new TableColumn<>("No. of Toppings");
        toppingsCol.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getNoToppings()).asObject());

        TableColumn<Order, Double> billCol = new TableColumn<>("Bill");
        billCol.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getBill()).asObject());

        pizzaOrdersTable.getColumns().addAll(customerNameCol, mobileCol, pizzaSizeCol, toppingsCol, billCol);

        // Populate table data
        refreshTable();
    }

    @FXML
    private void onAddButtonClick() {
        // Implement logic for adding a new order to the database
        String customerName = customerNameField.getText();
        String mobile = mobileNumberField.getText();
        String pizzaSize = getPizzaSize();
        int noToppings = Integer.parseInt(toppingsField.getText());
        double bill = calculateBill();

        try {
            String sql = "INSERT INTO orders (CustomerName, Mobile, PizzaSize, No_Toppings, Bill) " +
                    "VALUES (?, ?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, customerName);
            statement.setString(2, mobile);
            statement.setString(3, pizzaSize);
            statement.setInt(4, noToppings);
            statement.setDouble(5, bill);

            int rowsInserted = statement.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("A new order was added successfully!");
                ResultSet generatedKeys = statement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int id = generatedKeys.getInt(1);
                    Order newOrder = new Order(id, customerName, mobile, pizzaSize, noToppings, bill);
                    pizzaOrdersTable.getItems().add(newOrder);
                }
                clearFields();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle SQL exception
        }
    }

    @FXML
    private void onUpdateButtonClick() {
        // Get selected order from table
        Order selectedOrder = pizzaOrdersTable.getSelectionModel().getSelectedItem();
        if (selectedOrder == null) {
            // Handle case where no order is selected
            return;
        }

        // Retrieve updated values from UI
        String customerName = customerNameField.getText();
        String mobile = mobileNumberField.getText();
        String pizzaSize = getPizzaSize();
        int noToppings = Integer.parseInt(toppingsField.getText());
        double bill = calculateBill();

        try {
            // Prepare SQL statement for update
            String sql = "UPDATE orders SET CustomerName=?, Mobile=?, PizzaSize=?, No_Toppings=?, Bill=? WHERE Id=?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, customerName);
            statement.setString(2, mobile);
            statement.setString(3, pizzaSize);
            statement.setInt(4, noToppings);
            statement.setDouble(5, bill);
            statement.setInt(6, selectedOrder.getId());

            // Execute update statement
            int rowsUpdated = statement.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Order updated successfully!");
                selectedOrder.setCustomerName(customerName);
                selectedOrder.setMobile(mobile);
                selectedOrder.setPizzaSize(pizzaSize);
                selectedOrder.setNoToppings(noToppings);
                selectedOrder.setBill(bill);
                pizzaOrdersTable.refresh();
                clearFields();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle SQL exception
        }
    }

    @FXML
    private void onDeleteButtonClick() {
        // Get selected order from table
        Order selectedOrder = pizzaOrdersTable.getSelectionModel().getSelectedItem();
        if (selectedOrder == null) {
            // Handle case where no order is selected
            return;
        }

        try {
            // Prepare SQL statement for delete
            String sql = "DELETE FROM orders WHERE Id=?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, selectedOrder.getId());

            // Execute delete statement
            int rowsDeleted = statement.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("Order deleted successfully!");
                pizzaOrdersTable.getItems().remove(selectedOrder);
                clearFields();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle SQL exception
        }
    }

    @FXML
    private void onClearButtonClick() {
        clearFields();
    }

    @FXML
    private void onRefreshButtonClick() {
        refreshTable();
    }

    private String getPizzaSize() {
        StringBuilder sizeBuilder = new StringBuilder();
        if (xlCheckbox.isSelected()) {
            sizeBuilder.append("XL ");
        }
        if (lCheckbox.isSelected()) {
            sizeBuilder.append("L ");
        }
        if (mCheckbox.isSelected()) {
            sizeBuilder.append("M ");
        }
        if (sCheckbox.isSelected()) {
            sizeBuilder.append("S ");
        }
        return sizeBuilder.toString().trim();
    }

    public double calculateBill() {
        // Dummy calculation, replace with actual logic
        int toppings = Integer.parseInt(toppingsField.getText());
        return toppings * 1.5; // Assuming $1.50 per topping
    }

    private void refreshTable() {
        // Clear table and fetch updated data from database
        pizzaOrdersTable.getItems().clear();
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM orders");
            while (resultSet.next()) {
                Order order = new Order(
                        resultSet.getInt("Id"),
                        resultSet.getString("CustomerName"),
                        resultSet.getString("Mobile"),
                        resultSet.getString("PizzaSize"),
                        resultSet.getInt("No_Toppings"),
                        resultSet.getDouble("Bill")
                );
                pizzaOrdersTable.getItems().add(order);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle SQL exception
        }
    }

    private void clearFields() {
        customerNameField.clear();
        mobileNumberField.clear();
        xlCheckbox.setSelected(false);
        lCheckbox.setSelected(false);
        mCheckbox.setSelected(false);
        sCheckbox.setSelected(false);
        toppingsField.clear();
    }

    public void onHelloButtonClick(ActionEvent actionEvent) {
        // Placeholder for any additional action on button click
    }
}
