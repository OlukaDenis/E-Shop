package com.mcdenny.easyshopug.Model;

import android.widget.ImageView;

import java.io.Serializable;
import java.util.List;

public class Cart implements Serializable {
    private String Phone, Name, Image, Description, Price, quantity;

    public Cart(){
    }


    public Cart(String phone, String name, String image, String description, String price,  String quantity) {

        Phone = phone;
        Name = name;
        Image = image;
        Description = description;
        Price = price;
        this.quantity = quantity;
    }


    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getPrice() {
        return Price;
    }

    public void setPrice(String price) {
        Price = price;
    }


    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

}
