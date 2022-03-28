package com.example.pocketbank.model;
import java.util.Date;
public class Transaction
{

    int transactionId,userId;
    String recipient,type,description,date;
    double amount;

    public Transaction() {
    }

    public Transaction(int transactionId, int userId, String recipient, String type, String description, double amount, String date)
    {
        this.transactionId = transactionId;
        this.userId = userId;
        this.recipient = recipient;
        this.type = type;
        this.description = description;
        this.amount = amount;
        this.date = date;
    }

    public int getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(int id) {
        this.transactionId = id;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getUserId() {
        return userId;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public double getAmount() {
        return amount;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDate() {
        return date;
    }

    @Override
    public String toString()
    {
        return "Transaction{" +
                "id=" + transactionId +
                ", userId=" + userId +
                ", recipient='" + recipient + '\'' +
                ", type='" + type + '\'' +
                ", description='" + description + '\'' +
                ", amount=" + amount +
                ", date=" + date +
                '}';
    }
}
