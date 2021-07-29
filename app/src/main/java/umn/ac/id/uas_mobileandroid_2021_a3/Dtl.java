package umn.ac.id.uas_mobileandroid_2021_a3;

import java.io.Serializable;

public class Dtl implements Serializable {
    String date;
    String time;
    String hour;
    String location;

    public Dtl(String date, String time, String hour, String location) {
        this.date = date;
        this.time = time;
        this.hour = hour;
        this.location = location;
    }

    public Dtl() {
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getHour() {
        return hour;
    }

    public void setHour(String hour) {
        this.hour = hour;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
