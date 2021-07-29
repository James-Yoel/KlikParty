package umn.ac.id.uas_mobileandroid_2021_a3;

public class Users {
    private String username;
    private String email;
    private String phone;
    private String address;
    private String image;
    private float balance;

    public Users(String username, String email, String phone, String address, String image, float balance) {
        this.username = username;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.image = image;
        this.balance = balance;
    }

    public Users(){

    }

    public float getBalance() {
        return balance;
    }

    public void setBalance(float balance) {
        this.balance = balance;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getAddress() {
        return address;
    }

    public String getImage() {
        return image;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
