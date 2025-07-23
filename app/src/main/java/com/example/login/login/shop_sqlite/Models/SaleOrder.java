package com.example.login.login.shop_sqlite.Models;

import com.example.login.login.shop_sqlite.Dto.SaleOrderDetailResponseDto;

import java.math.BigDecimal;
import java.util.List;

public class SaleOrder {
    private Integer orderId;
    private Integer customerId;
    private Integer totalQuantity;
    private BigDecimal amountDue;
    private Integer paymentMethodId;
    private String orderNote;
    private Integer orderStatusId;
    private List<SaleOrderDetailResponseDto> orderDetails;

    public Integer getOrderId() { return orderId; }
    public void setOrderId(Integer orderId) { this.orderId = orderId; }

    public Integer getCustomerId() { return customerId; }
    public void setCustomerId(Integer customerId) { this.customerId = customerId; }

    public Integer getTotalQuantity() { return totalQuantity; }
    public void setTotalQuantity(Integer totalQuantity) { this.totalQuantity = totalQuantity; }

    public BigDecimal getAmountDue() { return amountDue; }
    public void setAmountDue(BigDecimal amountDue) { this.amountDue = amountDue; }

    public Integer getPaymentMethodId() { return paymentMethodId; }
    public void setPaymentMethodId(Integer paymentMethodId) { this.paymentMethodId = paymentMethodId; }

    public String getOrderNote() { return orderNote; }
    public void setOrderNote(String orderNote) { this.orderNote = orderNote; }

    public Integer getOrderStatusId() { return orderStatusId; }
    public void setOrderStatusId(Integer orderStatusId) { this.orderStatusId = orderStatusId; }

    public SaleOrder(Integer orderId, Integer customerId, Integer totalQuantity, BigDecimal amountDue, Integer paymentMethodId, String orderNote, Integer orderStatusId, List<SaleOrderDetailResponseDto> orderDetails) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.totalQuantity = totalQuantity;
        this.amountDue = amountDue;
        this.paymentMethodId = paymentMethodId;
        this.orderNote = orderNote;
        this.orderStatusId = orderStatusId;
        this.orderDetails = orderDetails;
    }

    public List<SaleOrderDetailResponseDto> getOrderDetails() {
        return orderDetails;
    }

    public void setOrderDetails(List<SaleOrderDetailResponseDto> orderDetails) {
        this.orderDetails = orderDetails;
    }
}