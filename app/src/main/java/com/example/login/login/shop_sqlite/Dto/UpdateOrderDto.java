package com.example.login.login.shop_sqlite.Dto;

import java.util.List;
import java.util.ArrayList;

public class UpdateOrderDto {
    private Integer customerId;
    private Integer paymentMethodId;
    private Integer orderStatusId;
    private String orderNote;
    private List<OrderDetailRequestDto> orderDetails = new ArrayList<>();


    public UpdateOrderDto(Integer customerId, Integer paymentMethodId, Integer orderStatusId, String orderNote, List<OrderDetailRequestDto> orderDetails) {
        this.customerId = customerId;
        this.paymentMethodId = paymentMethodId;
        this.orderStatusId = orderStatusId;
        this.orderNote = orderNote;
        this.orderDetails = orderDetails;
    }



    public Integer getCustomerId() {
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