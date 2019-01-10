package com.mcdenny.easyshopug.Model;

public class Order {
    private String OrderID;
    private String OrderName;
    private String Quantity;
    private String Price;
    private String Discount;

    public Order() {
    }

    public Order(String orderID, String orderName, String quantity, String price, String discount) {
        OrderID = orderID;
        OrderName = orderName;
        Quantity = quantity;
        Price = price;
        Discount = discount;
    }

    public String getOrderID() {
        return OrderID;
    }

    public void setOrderID(String orderID) {
        OrderID = orderID;
    }

    public String getOrderName() {
        return OrderName;
    }

    public void setOrderName(String orderName) {
        OrderName = orderName;
    }

    public String getQuantity() {
        return Quantity;
    }

    public void setQuantity(String quantity) {
        Quantity = quantity;
    }

    public String getPrice() {
        return Price;
    }

    public void setPrice(String price) {
        Price = price;
    }

    public String getDiscount() {
        return Discount;
    }

    public void setDiscount(String discount) {
        Discount = discount;
    }
}
