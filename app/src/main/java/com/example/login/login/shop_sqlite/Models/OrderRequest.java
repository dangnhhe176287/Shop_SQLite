package com.example.login.login.shop_sqlite.Models;
import java.util.List;

public class OrderRequest {
    private int customerId;
    private String customerName;
    private String phone;
    private String address;
    private double totalAmount;
    private double shippingFee;
    private List<OrderDetail> items;
    private String shippingAddress;
    private String orderNote;
    private int orderStatusId = 1;
    
    public OrderRequest(int customerId, String customerName, String phone, String address, double totalAmount, double shippingFee, List<OrderDetail> items, String shippingAddress, String orderNote) {
        this.customerId = customerId;
        this.customerName = customerName;
        this.phone = phone;
        this.address = address;
        this.totalAmount = totalAmount;
        this.shippingFee = shippingFee;
        this.items = items;
        this.shippingAddress = shippingAddress;
        this.orderNote = orderNote;
    }
    
    public int getCustomerId() { return customerId; }
    public void setCustomerId(int customerId) { this.customerId = customerId; }
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }
    public double getShippingFee() { return shippingFee; }
    public void setShippingFee(double shippingFee) { this.shippingFee = shippingFee; }
    public List<OrderDetail> getItems() { return items; }
    public void setItems(List<OrderDetail> items) { this.items = items; }
    public String getShippingAddress() { return shippingAddress; }
    public void setShippingAddress(String shippingAddress) { this.shippingAddress = shippingAddress; }
    public String getOrderNote() { return orderNote; }
    public void setOrderNote(String orderNote) { this.orderNote = orderNote; }
    public int getOrderStatusId() { return orderStatusId; }
    public void setOrderStatusId(int orderStatusId) { this.orderStatusId = orderStatusId; }

    public static class OrderDetail {
        private int productId;
        private String productName;
        private double price;
        private int quantity;
        private int variantId;
        private String variantAttributes;
        public OrderDetail(int productId, String productName, double price, int quantity, int variantId, String variantAttributes) {
            this.productId = productId;
            this.productName = productName;
            this.price = price;
            this.quantity = quantity;
            this.variantId = variantId;
            this.variantAttributes = variantAttributes;
        }
        public int getProductId() { return productId; }
        public void setProductId(int productId) { this.productId = productId; }
        public String getProductName() { return productName; }
        public void setProductName(String productName) { this.productName = productName; }
        public double getPrice() { return price; }
        public void setPrice(double price) { this.price = price; }
        public int getQuantity() { return quantity; }
        public void setQuantity(int quantity) { this.quantity = quantity; }
        public int getVariantId() { return variantId; }
        public void setVariantId(int variantId) { this.variantId = variantId; }
        public String getVariantAttributes() { return variantAttributes; }
        public void setVariantAttributes(String variantAttributes) { this.variantAttributes = variantAttributes; }
    }
} 