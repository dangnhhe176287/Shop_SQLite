package com.example.login.login.shop_sqlite.Models;
import java.util.List;
import java.util.Date;

public class OrderView {
    private int orderId;
    private String customerName;
    private String phone;
    private String address;
    private double amountDue;
    private Date createdAt;
    private List<OrderDetailView> items;
    // getters, setters, constructor
    public int getOrderId() { return orderId; }
    public void setOrderId(int orderId) { this.orderId = orderId; }
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public double getAmountDue() { return amountDue; }
    public void setAmountDue(double amountDue) { this.amountDue = amountDue; }
    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
    public List<OrderDetailView> getItems() { return items; }
    public void setItems(List<OrderDetailView> items) { this.items = items; }

    public static class OrderDetailView {
        private int productId;
        private String productName;
        private double price;
        private int quantity;
        public int getProductId() { return productId; }
        public void setProductId(int productId) { this.productId = productId; }
        public String getProductName() { return productName; }
        public void setProductName(String productName) { this.productName = productName; }
        public double getPrice() { return price; }
        public void setPrice(double price) { this.price = price; }
        public int getQuantity() { return quantity; }
        public void setQuantity(int quantity) { this.quantity = quantity; }
    }
} 