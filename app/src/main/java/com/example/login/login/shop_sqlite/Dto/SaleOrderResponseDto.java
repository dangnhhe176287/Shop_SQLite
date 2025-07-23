package com.example.login.login.shop_sqlite.Dto;

import java.util.List;

public class SaleOrderResponseDto {
    public int orderId;
    public int customerId;
    public int totalQuantity;
    public double amountDue;
    public int paymentMethodId;
    public String orderNote;
    public int orderStatusId;
    public List<SaleOrderDetailResponseDto> orderDetails;
}
