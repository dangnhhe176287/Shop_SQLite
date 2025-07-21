package com.example.login.login.shop_sqlite.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.login.login.shop_sqlite.Models.Order;
import com.example.login.login.shop_sqlite.R;

import java.util.List;

public class OrderAdapter extends ArrayAdapter<Order> {

    public OrderAdapter(Context context, List<Order> orders) {
        super(context, 0, orders);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Order order = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.item_order, parent, false);
        }

        TextView orderIdText = convertView.findViewById(R.id.orderIdTextView);
        TextView amountDueText = convertView.findViewById(R.id.amountDueTextView);
        TextView statusText = convertView.findViewById(R.id.statusTextView);

        orderIdText.setText("Mã đơn hàng: #" + order.getOrderId());
        amountDueText.setText("Tổng tiền: " + String.format("%.0f đ", order.getAmountDue()));
        statusText.setText("Trạng thái: " + getStatusText(order.getOrderStatusId()));

        return convertView;
    }

    private String getStatusText(int statusId) {
        switch (statusId) {
            case 1: return "Đang xử lý";
            case 2: return "Đã thanh toán";
            case 3: return "Đã giao";
            case 4: return "Đã hủy";
            default: return "Không rõ";
        }
    }
}