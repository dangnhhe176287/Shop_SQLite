package com.example.login.login.shop_sqlite.Models;

import com.example.login.login.shop_sqlite.Dto.SaleOrderDetailResponseDto; // Dùng SaleOrderDetailResponseDto

import java.util.List;

public class SaleOrder {
    private Integer orderId;
    private Integer customerId;
    private Double totalQuantity;
    private Double amountDue;
    private Integer paymentMethodId;
    private String orderNote;
    private Integer orderStatusId;
    private String shippingAddress;
    private String createdAt;
    private String updatedAt;
    private List<SaleOrderDetailResponseDto> orderDetails;

    public SaleOrder(Integer orderId, Integer customerId, Double totalQuantity, Double amountDue, Integer paymentMethodId, String orderNote, Integer orderStatusId, String shippingAddress, String createdAt, String updatedAt, List<SaleOrderDetailResponseDto> orderDetails) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.totalQuantity = totalQuantity;
        this.amountDue = amountDue;
        this.paymentMethodId = paymentMethodId;
        this.orderNote = orderNote;
        this.orderStatusId = orderStatusId;
        this.shippingAddress = shippingAddress;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.orderDetails = orderDetails;
    }

    public SaleOrder() {
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public Integer getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }

    public Double getTotalQuantity() {
        return totalQuantity;
    }

    public void setTotalQuantity(Double totalQuantity) {
        this.totalQuantity = totalQuantity;
    }

    public Double getAmountDue() {
        return amountDue;
    }

    public void setAmountDue(Double amountDue) {
        this.amountDue = amountDue;
    }

    public Integer getPaymentMethodId() {
        return paymentMethodId;
    }

    public void setPaymentMethodId(Integer paymentMethodId) {
        this.paymentMethodId = paymentMethodId;
    }

    public String getOrderNote() {
        return orderNote;
    }

    public void setOrderNote(String orderNote) {
        this.orderNote = orderNote;
    }

    public Integer getOrderStatusId() {
        return orderStatusId;
    }

    public void setOrderStatusId(Integer orderStatusId) {
        this.orderStatusId = orderStatusId;
    }

    public void setShippingAddress(String shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setOrderDetails(List<SaleOrderDetailResponseDto> orderDetails) {
        this.orderDetails = orderDetails;
    }

    public String getShippingAddress() { return shippingAddress; }
    public List<SaleOrderDetailResponseDto> getOrderDetails() { return orderDetails; }
}