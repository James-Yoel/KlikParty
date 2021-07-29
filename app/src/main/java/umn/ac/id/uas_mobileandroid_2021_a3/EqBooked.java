package umn.ac.id.uas_mobileandroid_2021_a3;

import java.io.Serializable;

public class EqBooked implements Serializable {
    Equipment equipment;
    int amount;

    public EqBooked(Equipment equipment, int amount) {
        this.equipment = equipment;
        this.amount = amount;
    }

    public EqBooked() {
    }

    public Equipment getEquipment() {
        return equipment;
    }

    public void setEquipment(Equipment equipment) {
        this.equipment = equipment;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}
