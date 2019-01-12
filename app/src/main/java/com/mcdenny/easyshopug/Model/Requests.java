package com.mcdenny.easyshopug.Model;

import java.util.ArrayList;
import java.util.List;

public class Requests {
    private String name;
    private String contact;
    private String address;
    private int total;
    private String status;
    private List<Cart> orders;//List of orders

    public Requests(){

    }

    public Requests(String name, String contact, String address, int total, List<Cart> orders) {
        this.name = name;
        this.contact = contact;
        this.address = address;
        this.total = total;
        this.status = "0";//default is 0 , 1: shipping, 2:shipped
        this.orders = orders;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<Cart> getOrders() {
        return orders;
    }

    public void setOrders(List<Cart> orders) {
        this.orders = orders;
    }
}
