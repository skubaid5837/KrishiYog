package com.example.krishiyog.models;

public class OrderModel {
    private String productId, userId, orderId, productQuantity, status, orderDate;

    // Empty constructor (Firebase needs it)
    public OrderModel() {}

    public OrderModel(String productId, String userId,String orderId, String productQuantity, String status, String orderDate) {
        this.productId = productId;
        this.userId = userId;
        this.orderId = orderId;
        this.productQuantity = productQuantity;
        this.status = status;
        this.orderDate = orderDate;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductQuantity() {
        return productQuantity;
    }

    public void setProductQuantity(String productQuantity) {
        this.productQuantity = productQuantity;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }
}

