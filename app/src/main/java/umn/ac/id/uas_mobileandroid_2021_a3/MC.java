package umn.ac.id.uas_mobileandroid_2021_a3;

import java.io.Serializable;

public class MC implements Serializable {
    private String name;
    private String image;
    private String detail;
    private String age;
    private String gender;
    private String personality;
    private float price;

    public MC(String name, String image, String detail, String age, String gender, String personality, float price) {
        this.name = name;
        this.image = image;
        this.detail = detail;
        this.age = age;
        this.gender = gender;
        this.personality = personality;
        this.price = price;
    }

    public MC(){

    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getPersonality() {
        return personality;
    }

    public void setPersonality(String personality) {
        this.personality = personality;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getName() {
        return name;
    }

    public String getImage() {
        return image;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
