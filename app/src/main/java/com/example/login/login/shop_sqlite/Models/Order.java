package com.example.login.login.shop_sqlite.Models;

import java.util.List;

public class Order {
    private int orderId;
    private int customerId;
    private int totalQuantity;
    private double amountDue;
    private int paymentMethodId;
    private String orderNote;
    private int orderStatusId;

    // GET & SET
    public int getOrderId() { return orderId; }
    public void setOrderId(int orderId) { this.orderId = orderId; }

    public int getCustomerId() { return customerId; }
    public void setCustomerId(int customerId) { this.customerId = customerId; }

    public int getTotalQuantity() { return totalQuantity; }
    public void setTotalQuantity(int totalQuantity) { this.totalQuantity = totalQuantity; }

    public double getAmountDue() { return amountDue; }
    public void setAmountDue(double amountDue) { this.amountDue = amountDue; }

    public int getPaymentMethodId() { return paymentMethodId; }
    public void setPaymentMethodId(int paymentMethodId) { this.paymentMethodId = paymentMethodId; }

    public String getOrderNote() { return orderNote; }
    public void setOrderNote(String orderNote) { this.orderNote = orderNote; }

    public int getOrderStatusId() { return orderStatusId; }
    public void setOrderStatusId(int orderStatusId) { this.orderStatusId = orderStatusId; }
}
