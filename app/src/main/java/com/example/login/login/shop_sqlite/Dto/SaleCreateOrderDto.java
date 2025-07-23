package com.example.login.login.shop_sqlite.Dto;

import com.example.login.login.shop_sqlite.Dto.SaleOrderDetailRequestDto;

import java.util.List;

public class SaleCreateOrderDto {
    private int customerId;
    private Integer paymentMethodId;
    private Integer orderStatusId;
    private String orderNote;
    private String shippingAddress;
    private double shippingFee;
    private List<SaleOrderDetailRequestDto> orderDetails;

    public SaleCreateOrderDto(int customerId, Integer paymentMethodId, String orderNote,
                              String shippingAddress, double shippingFee, List<SaleOrderDetailRequestDto> orderDetails) {
        this.customerId = customerId;
        this.paymentMethodId = paymentMethodId;
        this.orderNote = orderNote;
        this.shippingAddress = shippingAddress;
        this.shippingFee = shippingFee;
        this.orderDetails = orderDetails;
        this.orderStatusId = null;
    }

    public SaleCreateOrderDto(int customerId, Integer paymentMethodId, Integer orderStatusId, String orderNote,
                              String shippingAddress, double shippingFee, List<SaleOrderDetailRequestDto> orderDetails) {
        this.customerId = customerId;
        this.paymentMethodId = paymentMethodId;
        this.orderStatusId = orderStatusId;
        this.orderNote = orderNote;
        this.shippingAddress = shippingAddress;
        this.shippingFee = shippingFee;
        this.orderDetails = orderDetails;
    }

    public void setOrderStatusId(Integer orderStatusId) {
        this.orderStatusId = orderStatusId;
    }

    public int getCustomerId() { return customerId; }
    public Integer getPaymentMethodId() { return paymentMethodId; }
    public Integer getOrderStatusId() { return orderStatusId; }
    public String getOrderNote() { return orderNote; }
    public String getShippingAddress() { return shippingAddress; }
    public double getShippingFee() { return shippingFee; }
    public List<SaleOrderDetailRequestDto> getOrderDetails() { return orderDetails; }
}