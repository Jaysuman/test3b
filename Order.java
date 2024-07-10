package com.example.sumanpizzaapp;

public class Order {

    private int id; // Primary key
    private String customerName;
    private String mobile;
    private String pizzaSize;
    private int noToppings;
    private double bill;

    // Constructor
    public Order(int id, String customerName, String mobile, String pizzaSize, int noToppings, double bill) {
        this.id = id;
        this.customerName = customerName;
        this.mobile = mobile;
        this.pizzaSize = pizzaSize;
        this.noToppings = noToppings;
        this.bill = bill;
    }

    // Getter for Id
    public int getId() {
        return id;
    }

    // Setters and Getters for other fields
    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getPizzaSize() {
        return pizzaSize;
    }

    public void setPizzaSize(String pizzaSize) {
        this.pizzaSize = pizzaSize;
    }

    public int getNoToppings() {
        return noToppings;
    }

    public void setNoToppings(int noToppings) {
        this.noToppings = noToppings;
    }

    public double getBill() {
        return bill;
    }

    public void setBill(double bill) {
        this.bill = bill;
    }
}
