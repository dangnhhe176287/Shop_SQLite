package com.example.login.login.shop_sqlite.Models;
import java.util.List;
import com.google.gson.annotations.SerializedName;

public class OrderView {
    @SerializedName("orderId")
    private int orderId;
    @SerializedName("customerName")
    private String customerName;
    @SerializedName("phone")
    private String phone;
    @SerializedName("address")
    private String address;
    @SerializedName("shippingAddress")
    private String shippingAddress;
    @SerializedName("orderNote")
    private String orderNote;
    @SerializedName("amountDue")
    private double amountDue;
    @SerializedName("orderStatusId")
    private int orderStatusId;
    @SerializedName("orderStatusTitle")
    private String orderStatusTitle;
    @SerializedName("createdAt")
    private String createdAt;
    @SerializedName("updatedAt")
    private String updatedAt;
    @SerializedName("items")
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
    public String getShippingAddress() { return shippingAddress; }
    public void setShippingAddress(String shippingAddress) { this.shippingAddress = shippingAddress; }
    public String getOrderNote() { return orderNote; }
    public void setOrderNote(String orderNote) { this.orderNote = orderNote; }
    public double getAmountDue() { return amountDue; }
    public void setAmountDue(double amountDue) { this.amountDue = amountDue; }
    public int getOrderStatusId() { return orderStatusId; }
    public void setOrderStatusId(int orderStatusId) { this.orderStatusId = orderStatusId; }
    public String getOrderStatusTitle() { return orderStatusTitle; }
    public void setOrderStatusTitle(String orderStatusTitle) { this.orderStatusTitle = orderStatusTitle; }
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }
    public List<OrderDetailView> getItems() { return items; }
    public void setItems(List<OrderDetailView> items) { this.items = items; }

    public static class OrderDetailView {
        @SerializedName("productId")
        private int productId;
        @SerializedName("productName")
        private String productName;
        @SerializedName("price")
        private double price;
        @SerializedName("quantity")
        private int quantity;
        @SerializedName("variantAttributes")
        private String variantAttributes;
        
        public int getProductId() { return productId; }
        public void setProductId(int productId) { this.productId = productId; }
        public String getProductName() { return productName; }
        public void setProductName(String productName) { this.productName = productName; }
        public double getPrice() { return price; }
        public void setPrice(double price) { this.price = price; }
        public int getQuantity() { return quantity; }
        public void setQuantity(int quantity) { this.quantity = quantity; }
        public String getVariantAttributes() { return variantAttributes; }
        public void setVariantAttributes(String variantAttributes) { this.variantAttributes = variantAttributes; }
    }
} 