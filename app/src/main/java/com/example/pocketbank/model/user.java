package com.example.pocketbank.model;
public class user
{
    private int userId;
    private String password;
    private String email;
    private String name;
    private String address;
    private double remainedAmount;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getRemainedAmount() {
        return remainedAmount;
    }

    public void setRemainedAmount(double remainedAmount) {
        this.remainedAmount = remainedAmount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public user()
    {
    }
    public user(int userId, String password, String email, String name, String address,double remainedAmount)
    {
        this.userId = userId;
        this.password = password;
        this.email = email;
        this.name = name;
        this.address = address;
        this.remainedAmount = remainedAmount;
    }
}
