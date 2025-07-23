package com.example.login.login.shop_sqlite.Dto;

import java.util.List;

public class SaleOrderResponseDto {
    private int orderId;
    private Integer customerId;
    private Integer totalQuantity;
    private Double amountDue;
    private Integer paymentMethodId;
    private String orderNote;
    private Integer orderStatusId;
    private String createdAt;
    private String updatedAt;
    private String shippingAddress;
    private List<SaleOrderDetailResponseDto> orderDetails;

    // getters & setters
    public int getOrderId() { return orderId; }
    public void setOrderId(int orderId) { this.orderId = orderId; }

    public Integer getCustomerId() { return customerId; }
    public void setCustomerId(Integer customerId) { this.customerId = customerId; }

    public Integer getTotalQuantity() { return totalQuantity; }
    public void setTotalQuantity(Integer totalQuantity) { this.totalQuantity = totalQuantity; }

    public Double getAmountDue() { return amountDue; }
    public void setAmountDue(Double amountDue) { this.amountDue = amountDue; }

    public Integer getPaymentMethodId() { return paymentMethodId; }
    public void setPaymentMethodId(Integer paymentMethodId) { this.paymentMethodId = paymentMethodId; }

    public String getOrderNote() { return orderNote; }
    public void setOrderNote(String orderNote) { this.orderNote = orderNote; }

    public Integer getOrderStatusId() { return orderStatusId; }
    public void setOrderStatusId(Integer orderStatusId) { this.orderStatusId = orderStatusId; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }

    public String getShippingAddress() { return shippingAddress; }
    public void setShippingAddress(String shippingAddress) { this.shippingAddress = shippingAddress; }

    public List<SaleOrderDetailResponseDto> getOrderDetails() { return orderDetails; }
    public void setOrderDetails(List<SaleOrderDetailResponseDto> orderDetails) { this.orderDetails = orderDetails; }
}
