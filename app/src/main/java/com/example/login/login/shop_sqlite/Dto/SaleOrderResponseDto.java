package com.example.login.login.shop_sqlite.Dto;

import java.util.List;

public class SaleOrderResponseDto {
    private Integer orderId;
    private Integer customerId;
    private double totalQuantity;
    private double amountDue;
    private Integer paymentMethodId;
    private String orderNote;
    private Integer orderStatusId;
    private String shippingAddress;
    private String createdAt;
    private String updatedAt;
    private List<SaleOrderDetailResponseDto> orderDetails;
    public SaleOrderResponseDto(Integer orderId, Integer customerId, double totalQuantity, double amountDue,
                                Integer paymentMethodId, String orderNote, Integer orderStatusId, String shippingAddress,
                                String createdAt, String updatedAt, List<SaleOrderDetailResponseDto> orderDetails) {
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

    public Integer getOrderId() { return orderId; }
    public Integer getCustomerId() { return customerId; }
    public double getTotalQuantity() { return totalQuantity; }
    public double getAmountDue() { return amountDue; }
    public Integer getPaymentMethodId() { return paymentMethodId; }
    public String getOrderNote() { return orderNote; }
    public Integer getOrderStatusId() { return orderStatusId; }
    public String getShippingAddress() { return shippingAddress; }
    public String getCreatedAt() { return createdAt; }
    public String getUpdatedAt() { return updatedAt; }
    public List<SaleOrderDetailResponseDto> getOrderDetails() { return orderDetails; }
}