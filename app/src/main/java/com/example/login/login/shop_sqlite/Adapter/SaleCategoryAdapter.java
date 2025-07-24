package com.example.login.login.shop_sqlite.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.example.login.login.shop_sqlite.Dto.SaleProductCategoryResponseDto;
import com.example.login.login.shop_sqlite.R;

import java.util.List;

public class SaleCategoryAdapter extends ArrayAdapter<SaleProductCategoryResponseDto> {

    private final Context context;
    private final List<SaleProductCategoryResponseDto> categories;
    private OnCategoryActionListener listener;

    public interface OnCategoryActionListener {
        void onEditClick(int categoryId);
        void onDeleteClick(int categoryId);
    }

    public SaleCategoryAdapter(@NonNull Context context, List<SaleProductCategoryResponseDto> categories, OnCategoryActionListener listener) {
        super(context, 0, categories);
        this.context = context;
        this.categories = categories;
        this.listener = listener;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.sale_item_category, parent, false);
        }

        SaleProductCategoryResponseDto currentCategory = categories.get(position);

        TextView txtCategoryTitle = convertView.findViewById(R.id.txtCategoryTitle);
        Button btnEditCategory = convertView.findViewById(R.id.btnEditCategory);
        Button btnDeleteCategory = convertView.findViewById(R.id.btnDeleteCategory);

        txtCategoryTitle.setText(currentCategory.getProductCategoryTitle());

        btnEditCategory.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEditClick(currentCategory.getProductCategoryId());
            }
        });

        btnDeleteCategory.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDeleteClick(currentCategory.getProductCategoryId());
            }
        });

        return convertView;
    }
}