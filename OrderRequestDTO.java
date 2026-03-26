package com.example.E_COMMERCE.Backend.DTO;

public class OrderRequestDTO {

    private String userEmail;
    private double totalAmount;

    public OrderRequestDTO() {}

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }
}
