package com.example.login.login.shop_sqlite.Activity;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.example.login.login.shop_sqlite.Api.ApiClient;
import com.example.login.login.shop_sqlite.Api.SaleApiService;
import com.example.login.login.shop_sqlite.Dto.SaleCreateProductDto;
import com.example.login.login.shop_sqlite.Dto.SaleCreateProductImageDto;
import com.example.login.login.shop_sqlite.Dto.SaleCreateProductVariantDto;
import com.example.login.login.shop_sqlite.Dto.SaleProductResponseDto;
import com.example.login.login.shop_sqlite.R;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.*;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SaleCreateProductActivity extends AppCompatActivity {

    private static final String TAG = "CreateProductActivity";

    private EditText edtName, edtDescription, edtCategoryId, edtBrand, edtBasePrice, edtStatus,
            edtImageUrl, edtAvailableSizes, edtAvailableColors;
    private CheckBox chkIsDelete;
    private LinearLayout variantsContainer;
    private Button btnAddVariant, btnCreate;

    private Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sale_activity_create_product);

        initViews();
        addVariantField(); // mặc định 1 variant

        btnAddVariant.setOnClickListener(v -> addVariantField());
        btnCreate.setOnClickListener(v -> createProduct());
    }

    private void initViews() {
        edtName = findViewById(R.id.edtName);
        edtDescription = findViewById(R.id.edtDescription);
        edtCategoryId = findViewById(R.id.edtCategoryId);
        edtBrand = findViewById(R.id.edtBrand);
        edtBasePrice = findViewById(R.id.edtBasePrice);
        edtStatus = findViewById(R.id.edtStatus);
        edtImageUrl = findViewById(R.id.edtImageUrl);
        edtAvailableSizes = findViewById(R.id.edtAvailableSizes);
        edtAvailableColors = findViewById(R.id.edtAvailableColors);
        chkIsDelete = findViewById(R.id.chkIsDelete);
        variantsContainer = findViewById(R.id.variantsContainer);
        btnAddVariant = findViewById(R.id.btnAddVariant);
        btnCreate = findViewById(R.id.btnCreate);
    }

    private void addVariantField() {
        View variantView = LayoutInflater.from(this).inflate(R.layout.variant_item, variantsContainer, false);
        variantsContainer.addView(variantView);
        Log.d(TAG, "Đã thêm một trường biến thể mới.");
    }

    private void createProduct() {
        Log.d(TAG, "Bắt đầu quá trình tạo sản phẩm...");
        try {
            // Lấy dữ liệu cơ bản
            String name = edtName.getText().toString().trim();
            String description = edtDescription.getText().toString().trim();
            String categoryIdStr = edtCategoryId.getText().toString().trim();
            Integer categoryId = categoryIdStr.isEmpty() ? null : Integer.parseInt(categoryIdStr);
            String brand = edtBrand.getText().toString().trim();
            String basePriceStr = edtBasePrice.getText().toString().trim();
            double basePrice = basePriceStr.isEmpty() ? 0.0 : Double.parseDouble(basePriceStr);
            String statusStr = edtStatus.getText().toString().trim();
            Integer status = statusStr.isEmpty() ? null : Integer.parseInt(statusStr);
            String imageUrl = edtImageUrl.getText().toString().trim();
            boolean isDelete = chkIsDelete.isChecked();
            String availableSizesInput = edtAvailableSizes.getText().toString().trim();
            String availableColorsInput = edtAvailableColors.getText().toString().trim();

            // Validate bắt buộc
            if (name.isEmpty() || description.isEmpty() || brand.isEmpty() || basePriceStr.isEmpty()) {
                Toast.makeText(this, "Vui lòng điền đầy đủ các trường bắt buộc", Toast.LENGTH_LONG).show();
                return;
            }

            // Parse available attributes
            Map<String, List<String>> availableAttributesMap = new HashMap<>();
            if (!availableSizesInput.isEmpty()) {
                List<String> sizesList = new ArrayList<>();
                for (String s : availableSizesInput.split("[,.]")) {
                    if (!s.trim().isEmpty()) sizesList.add(s.trim());
                }
                if (!sizesList.isEmpty()) availableAttributesMap.put("size", sizesList);
            }
            if (!availableColorsInput.isEmpty()) {
                List<String> colorsList = new ArrayList<>();
                for (String c : availableColorsInput.split("[,.]")) {
                    if (!c.trim().isEmpty()) colorsList.add(c.trim());
                }
                if (!colorsList.isEmpty()) availableAttributesMap.put("color", colorsList);
            }
            String availableAttributesJson = gson.toJson(availableAttributesMap);

            // Lấy variants từ UI
            List<SaleCreateProductVariantDto> variantsList = new ArrayList<>();
            for (int i = 0; i < variantsContainer.getChildCount(); i++) {
                View variantView = variantsContainer.getChildAt(i);
                EditText edtVariantSize = variantView.findViewById(R.id.edtVariantSize);
                EditText edtVariantColor = variantView.findViewById(R.id.edtVariantColor);
                EditText edtVariantPrice = variantView.findViewById(R.id.edtVariantPrice);
                EditText edtVariantStock = variantView.findViewById(R.id.edtVariantStock);

                String size = edtVariantSize.getText().toString().trim();
                String color = edtVariantColor.getText().toString().trim();
                String priceStr = edtVariantPrice.getText().toString().trim();
                String stockStr = edtVariantStock.getText().toString().trim();

                if (size.isEmpty() && color.isEmpty() && priceStr.isEmpty() && stockStr.isEmpty()) {
                    continue;
                }

                JsonObject attributesObject = new JsonObject();
                if (!size.isEmpty()) attributesObject.addProperty("size", size);
                if (!color.isEmpty()) attributesObject.addProperty("color", color);

                JsonObject variantDetailsObject = new JsonObject();
                try {
                    if (!priceStr.isEmpty()) {
                        double price = Double.parseDouble(priceStr);
                        variantDetailsObject.addProperty("price", price);
                    }
                } catch (NumberFormatException e) {
                    Toast.makeText(this, "Giá biến thể không hợp lệ tại vị trí " + (i + 1), Toast.LENGTH_LONG).show();
                    return;
                }
                try {
                    if (!stockStr.isEmpty()) {
                        int stock = Integer.parseInt(stockStr);
                        variantDetailsObject.addProperty("stock", stock);
                    }
                } catch (NumberFormatException e) {
                    Toast.makeText(this, "Số lượng biến thể không hợp lệ tại vị trí " + (i + 1), Toast.LENGTH_LONG).show();
                    return;
                }

                String attributesJsonForVariant = gson.toJson(attributesObject);
                String variantsJsonForVariant = gson.toJson(variantDetailsObject);
                variantsList.add(new SaleCreateProductVariantDto(attributesJsonForVariant, variantsJsonForVariant));
            }

            // Lấy images
            List<SaleCreateProductImageDto> images = imageUrl.isEmpty() ? null :
                    Collections.singletonList(new SaleCreateProductImageDto(imageUrl));

            // Tạo DTO gửi API
            SaleCreateProductDto productDto = new SaleCreateProductDto(
                    name, description, categoryId, brand, basePrice,
                    availableAttributesJson, status, isDelete, images, variantsList
            );

            String json = gson.toJson(productDto);
            Log.i(TAG, "JSON request gửi đến API: \n" + json);

            SaleApiService saleApiService = ApiClient.getClient().create(SaleApiService.class);
            Call<SaleProductResponseDto> call = saleApiService.createProduct(productDto);

            call.enqueue(new Callback<SaleProductResponseDto>() {
                @Override
                public void onResponse(Call<SaleProductResponseDto> call, Response<SaleProductResponseDto> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Toast.makeText(SaleCreateProductActivity.this, "Tạo sản phẩm thành công!", Toast.LENGTH_SHORT).show();
                        setResult(RESULT_OK);
                        finish();
                    } else {
                        Toast.makeText(SaleCreateProductActivity.this, "Lỗi khi tạo sản phẩm!", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<SaleProductResponseDto> call, Throwable t) {
                    Toast.makeText(SaleCreateProductActivity.this, "Không thể kết nối đến máy chủ!", Toast.LENGTH_LONG).show();
                }
            });

        } catch (Exception e) {
            Toast.makeText(this, "Có lỗi xảy ra: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
