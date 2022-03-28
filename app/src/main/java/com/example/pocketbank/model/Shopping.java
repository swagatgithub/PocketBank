package com.example.pocketbank.model;
public class Shopping
{
    int userId,itemId,transactionId,id;
    String description,date;
    double price;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public int getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(int transactionId) {
        this.transactionId = transactionId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public Shopping(int userId, int itemId, int transactionId, int id, String description, String date, double price) {
        this.userId = userId;
        this.itemId = itemId;
        this.transactionId = transactionId;
        this.id = id;
        this.description = description;
        this.date = date;
        this.price = price;
    }

    @Override
    public String toString() {
        return "Shopping{" +
                "userId=" + userId +
                ", itemId=" + itemId +
                ", transactionId=" + transactionId +
                ", id=" + id +
                ", description='" + description + '\'' +
                ", date='" + date + '\'' +
                ", price=" + price +
                '}';
    }

    public Shopping() {
    }
}
