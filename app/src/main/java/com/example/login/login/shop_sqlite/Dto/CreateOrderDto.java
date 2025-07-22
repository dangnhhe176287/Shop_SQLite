package com.example.login.login.shop_sqlite.Dto;

import java.util.List;

public class CreateOrderDto {
    private int customerId;
    private Integer paymentMethodId; // Integer để khớp với int? của C#
    private Integer orderStatusId;   // Integer để khớp với int? của C# (nếu backend yêu cầu)
    private String orderNote;
    private List<OrderDetailRequestDto> orderDetails;

    // Constructor phải khớp với các trường bạn muốn gửi
    public CreateOrderDto(int customerId, Integer paymentMethodId, Integer orderStatusId, String orderNote, List<OrderDetailRequestDto> orderDetails) {
        this.customerId = customerId;
        this.paymentMethodId = paymentMethodId;
        this.orderStatusId = orderStatusId;
        this.orderNote = orderNote;
        this.orderDetails = orderDetails;
    }

    // Getters (cần thiết cho Gson để serialize)
    public int getCustomerId() {
        return customerId;
    }

    public Integer getPaymentMethodId() {
        return paymentMethodId;
    }

    public Integer getOrderStatusId() {
        return orderStatusId;
    }

    public String getOrderNote() {
        return orderNote;
    }

    public List<OrderDetailRequestDto> getOrderDetails() {
        return orderDetails;
    }
}