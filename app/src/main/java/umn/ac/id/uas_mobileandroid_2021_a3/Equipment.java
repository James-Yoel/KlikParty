package umn.ac.id.uas_mobileandroid_2021_a3;

import java.io.Serializable;

public class Equipment implements Serializable {
    public String name;
    public String image;
    public String detail;
    public String color;
    public String size;
    public int amount;
    public float price;

    public Equipment(String name, String image, String detail, String color, String size, int amount, float price) {
        this.name = name;
        this.image = image;
        this.detail = detail;
        this.color = color;
        this.size = size;
        this.amount = amount;
        this.price = price;
    }

    public Equipment() {
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
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

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}
