package umn.ac.id.uas_mobileandroid_2021_a3;

import android.os.Parcelable;

import java.io.Serializable;

public class Party implements Serializable {
    String name;
    String packageType;
    String scale;
    String inOutDoor;

    public Party(String name, String packageType, String scale, String inOutDoor) {
        this.name = name;
        this.packageType = packageType;
        this.scale = scale;
        this.inOutDoor = inOutDoor;
    }

    public Party(){

    }

    public String getName() {
        return name;
    }

    public String getPackageType() {
        return packageType;
    }

    public String getScale() {
        return scale;
    }

    public String getInOutDoor() {
        return inOutDoor;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPackageType(String packageType) {
        this.packageType = packageType;
    }

    public void setScale(String scale) {
        this.scale = scale;
    }

    public void setInOutDoor(String inOutDoor) {
        this.inOutDoor = inOutDoor;
    }
}
