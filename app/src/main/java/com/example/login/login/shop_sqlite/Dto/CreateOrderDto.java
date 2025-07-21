package com.example.login.login.shop_sqlite.Dto;

import java.util.List;

public class CreateOrderDto {
    public int customerId;
    public int paymentMethodId;
    public String orderNote;
    public List<OrderDetailDto> orderDetails;

    public CreateOrderDto() {}
    public CreateOrderDto(int customerId, int paymentMethodId, String orderNote, List<OrderDetailDto> orderDetails) {
        this.customerId = customerId;
        this.paymentMethodId = paymentMethodId;
        this.orderNote = orderNote;
        this.orderDetails = orderDetails;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public int getPaymentMethodId() {
        return paymentMethodId;
    }

    public void setPaymentMethodId(int paymentMethodId) {
        this.paymentMethodId = paymentMethodId;
    }

    public String getOrderNote() {
        return orderNote;
    }

    public void setOrderNote(String orderNote) {
        this.orderNote = orderNote;
    }

    public List<OrderDetailDto> getOrderDetails() {
        return orderDetails;
    }

    public void setOrderDetails(List<OrderDetailDto> orderDetails) {
        this.orderDetails = orderDetails;
    }
}
