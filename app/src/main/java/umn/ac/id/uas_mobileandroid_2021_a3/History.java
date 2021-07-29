package umn.ac.id.uas_mobileandroid_2021_a3;

import java.io.Serializable;

public class History implements Serializable {
    public String orderId;
    public String bookType;
    public String orderMade;
    public String bookDate;
    public String bookTime;
    public String bookLocation;
    public float bookPrice;

    public History(String orderId, String bookType, String orderMade, String bookDate, String bookTime, String bookLocation, float bookPrice) {
        this.orderId = orderId;
        this.bookType = bookType;
        this.orderMade = orderMade;
        this.bookDate = bookDate;
        this.bookTime = bookTime;
        this.bookLocation = bookLocation;
        this.bookPrice = bookPrice;
    }

    public History(){

    }

    public float getBookPrice() {
        return bookPrice;
    }

    public void setBookPrice(float bookPrice) {
        this.bookPrice = bookPrice;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getBookType() {
        return bookType;
    }

    public void setBookType(String bookType) {
        this.bookType = bookType;
    }

    public String getOrderMade() {
        return orderMade;
    }

    public void setOrderMade(String orderMade) {
        this.orderMade = orderMade;
    }

    public String getBookDate() {
        return bookDate;
    }

    public void setBookDate(String bookDate) {
        this.bookDate = bookDate;
    }

    public String getBookTime() {
        return bookTime;
    }

    public void setBookTime(String bookTime) {
        this.bookTime = bookTime;
    }

    public String getBookLocation() {
        return bookLocation;
    }

    public void setBookLocation(String bookLocation) {
        this.bookLocation = bookLocation;
    }
}
