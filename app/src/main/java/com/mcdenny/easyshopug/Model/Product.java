package com.mcdenny.easyshopug.Model;

import java.io.Serializable;

public class Product implements Serializable {
    private String name, image, description, price, addedBy, bargained, available, menuid;
    public Product() {
    }

    public Product(String name, String image, String description, String price, String addedBy, String bargained, String available, String menuid) {
        this.name = name;
        this.image = image;
        this.description = description;
        this.price = price;
        this.addedBy = addedBy;
        this.bargained = bargained;
        this.available = available;
        this.menuid = menuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getAddedBy() {
        return addedBy;
    }

    public void setAddedBy(String addedBy) {
        this.addedBy = addedBy;
    }

    public String getBargained() {
        return bargained;
    }

    public void setBargained(String bargained) {
        this.bargained = bargained;
    }

    public String getAvailable() {
        return available;
    }

    public void setAvailable(String available) {
        this.available = available;
    }

    public String getMenuid() {
        return menuid;
    }

    public void setMenuid(String menuid) {
        this.menuid = menuid;
    }
}
