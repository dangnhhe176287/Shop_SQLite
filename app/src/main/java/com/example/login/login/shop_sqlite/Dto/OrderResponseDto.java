package com.example.login.login.shop_sqlite.Dto;

import java.util.List;

public class OrderResponseDto {
    public int orderId;
    public int customerId;
    public int totalQuantity;
    public double amountDue;
    public int paymentMethodId;
    public String orderNote;
    public int orderStatusId;
    public List<OrderDetailResponseDto> orderDetails;
}
