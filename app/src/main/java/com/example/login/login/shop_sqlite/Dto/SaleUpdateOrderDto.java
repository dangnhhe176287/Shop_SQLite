package com.example.login.login.shop_sqlite.Dto;

import java.util.List;

public class SaleUpdateOrderDto {
    private Integer customerId; // Nullable
    private Integer paymentMethodId;
    private Integer orderStatusId;
    private String orderNote;
    private String shippingAddress;
    private List<SaleOrderDetailRequestDto> orderDetails;

    public SaleUpdateOrderDto(Integer customerId, Integer paymentMethodId, Integer orderStatusId,
                              String orderNote, String shippingAddress,
                              List<SaleOrderDetailRequestDto> orderDetails) {
        this.customerId = customerId;
        this.paymentMethodId = paymentMethodId;
        this.orderStatusId = orderStatusId;
        this.orderNote = orderNote;
        this.shippingAddress = shippingAddress;
        this.orderDetails = orderDetails;
    }

    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }

    public Integer getCustomerId() { return customerId; }
    public Integer getPaymentMethodId() { return paymentMethodId; }
    public Integer getOrderStatusId() { return orderStatusId; }
    public String getOrderNote() { return orderNote; }
    public String getShippingAddress() { return shippingAddress; }
    public List<SaleOrderDetailRequestDto> getOrderDetails() { return orderDetails; }
}