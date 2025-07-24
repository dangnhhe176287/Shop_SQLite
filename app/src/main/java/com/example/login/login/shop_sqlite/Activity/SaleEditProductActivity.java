package com.example.login.login.shop_sqlite.Activity;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.login.login.shop_sqlite.Api.ApiClient;
import com.example.login.login.shop_sqlite.Api.SaleApiService;
import com.example.login.login.shop_sqlite.Dto.SaleUpdateProductDto;
import com.example.login.login.shop_sqlite.Dto.SaleUpdateProductImageDto;
import com.example.login.login.shop_sqlite.Dto.SaleUpdateProductVariantDto;
import com.example.login.login.shop_sqlite.Dto.SaleProductResponseDto;
import com.example.login.login.shop_sqlite.Dto.SaleProductVariantDto;
import com.example.login.login.shop_sqlite.R;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SaleEditProductActivity extends AppCompatActivity {

    private static final String TAG = "EditProductActivity";

    private EditText edtProductId, edtName, edtDescription, edtCategoryId, edtBrand, edtBasePrice, edtStatus, edtImageUrl, edtAvailableSizes, edtAvailableColors;
    private CheckBox chkIsDelete;
    private LinearLayout variantsContainer;
    private Button btnAddVariant, btnUpdate;

    private Gson gson = new Gson();
    private SaleApiService saleApiService;
    private int productIdToEdit;

    private int productImageId = 0;  // Lưu ID ảnh để gửi lại khi update
    private List<Integer> variantIds = new ArrayList<>(); // Lưu danh sách ID variant

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sale_activity_edit_product);

        initViews();
        saleApiService = ApiClient.getClient().create(SaleApiService.class);

        if (getIntent().hasExtra("productId")) {
            productIdToEdit = getIntent().getIntExtra("productId", -1);
            if (productIdToEdit != -1) {
                edtProductId.setText(String.valueOf(productIdToEdit));
                fetchProductDetails(productIdToEdit);
            } else {
                Toast.makeText(this, "Không tìm thấy ID sản phẩm hợp lệ.", Toast.LENGTH_SHORT).show();
                finish();
            }
        } else {
            Toast.makeText(this, "Không có ID sản phẩm được cung cấp.", Toast.LENGTH_SHORT).show();
            finish();
        }

        btnAddVariant.setOnClickListener(v -> addVariantField(null, null, 0.0, 0));
        btnUpdate.setOnClickListener(v -> updateProduct());
    }

    private void initViews() {
        edtProductId = findViewById(R.id.edtProductId);
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
        btnUpdate = findViewById(R.id.btnUpdate);
    }

    private void fetchProductDetails(int productId) {
        Log.d(TAG, "Đang lấy chi tiết sản phẩm cho ID: " + productId);
        saleApiService.getProductById(productId).enqueue(new Callback<SaleProductResponseDto>() {
            @Override
            public void onResponse(Call<SaleProductResponseDto> call, Response<SaleProductResponseDto> response) {
                if (response.isSuccessful() && response.body() != null) {
                    SaleProductResponseDto product = response.body();
                    displayProductDetails(product);
                    Log.i(TAG, "Đã lấy chi tiết sản phẩm thành công.");
                } else {
                    Toast.makeText(SaleEditProductActivity.this, "Lỗi khi lấy chi tiết sản phẩm: " + response.code(), Toast.LENGTH_LONG).show();
                    finish();
                }
            }

            @Override
            public void onFailure(Call<SaleProductResponseDto> call, Throwable t) {
                Toast.makeText(SaleEditProductActivity.this, "Lỗi kết nối khi lấy chi tiết sản phẩm: " + t.getMessage(), Toast.LENGTH_LONG).show();
                finish();
            }
        });
    }

    private void displayProductDetails(SaleProductResponseDto product) {
        edtName.setText(product.getName());
        edtDescription.setText(product.getDescription());
        edtCategoryId.setText(product.getProductCategoryId() != null ? String.valueOf(product.getProductCategoryId()) : "");
        edtBrand.setText(product.getBrand());
        edtBasePrice.setText(String.valueOf(product.getBasePrice()));
        edtStatus.setText(product.getStatus() != null ? String.valueOf(product.getStatus()) : "");
        chkIsDelete.setChecked(product.isDelete());

        if (product.getProductImages() != null && !product.getProductImages().isEmpty()) {

            productImageId = product.getProductImages().get(0).getProductImageId();
            edtImageUrl.setText(product.getProductImages().get(0).getImageUrl());
        }


        if (product.getAvailableAttributes() != null && !product.getAvailableAttributes().isEmpty()) {
            try {
                Type type = new TypeToken<Map<String, List<String>>>(){}.getType();
                Map<String, List<String>> attributesMap = gson.fromJson(product.getAvailableAttributes(), type);
                if (attributesMap.containsKey("size")) {
                    edtAvailableSizes.setText(android.text.TextUtils.join(", ", attributesMap.get("size")));
                }
                if (attributesMap.containsKey("color")) {
                    edtAvailableColors.setText(android.text.TextUtils.join(", ", attributesMap.get("color")));
                }
            } catch (Exception e) {
                Log.e(TAG, "Lỗi parse AvailableAttributes: " + e.getMessage(), e);
            }
        }

        variantsContainer.removeAllViews();
        variantIds.clear();
        if (product.getVariants() != null && !product.getVariants().isEmpty()) {
            for (SaleProductVariantDto variantDto : product.getVariants()) {
                variantIds.add(variantDto.getVariantId());

                String size = "";
                String color = "";
                double price = 0.0;
                int stock = 0;

                if (!variantDto.getAttributes().isEmpty()) {
                    try {
                        Type attributesType = new TypeToken<Map<String, String>>(){}.getType();
                        Map<String, String> attributesMap = gson.fromJson(variantDto.getAttributes(), attributesType);
                        if (attributesMap.containsKey("size")) size = attributesMap.get("size");
                        if (attributesMap.containsKey("color")) color = attributesMap.get("color");
                    } catch (Exception e) {
                        Log.e(TAG, "Lỗi parse attributes JSON: " + e.getMessage(), e);
                    }
                }

                if (!variantDto.getVariants().isEmpty()) {
                    try {
                        Type variantDetailsType = new TypeToken<Map<String, Object>>(){}.getType();
                        Map<String, Object> variantDetailsMap = gson.fromJson(variantDto.getVariants(), variantDetailsType);
                        if (variantDetailsMap.containsKey("price")) {
                            Object priceObj = variantDetailsMap.get("price");
                            if (priceObj instanceof Double) price = (Double) priceObj;
                            else if (priceObj instanceof String) price = Double.parseDouble((String) priceObj);
                        }
                        if (variantDetailsMap.containsKey("stock")) {
                            Object stockObj = variantDetailsMap.get("stock");
                            if (stockObj instanceof Double) stock = ((Double) stockObj).intValue();
                            else if (stockObj instanceof String) stock = Integer.parseInt((String) stockObj);
                            else if (stockObj instanceof Integer) stock = (Integer) stockObj;
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Lỗi parse variants JSON: " + e.getMessage(), e);
                    }
                }
                addVariantField(size, color, price, stock);
            }
        }
    }

    private void addVariantField(String size, String color, double price, int stock) {
        View variantView = LayoutInflater.from(this).inflate(R.layout.variant_item, variantsContainer, false);
        EditText edtVariantSize = variantView.findViewById(R.id.edtVariantSize);
        EditText edtVariantColor = variantView.findViewById(R.id.edtVariantColor);
        EditText edtVariantPrice = variantView.findViewById(R.id.edtVariantPrice);
        EditText edtVariantStock = variantView.findViewById(R.id.edtVariantStock);
        Button btnRemoveVariant = variantView.findViewById(R.id.btnRemoveVariant);

        edtVariantSize.setText(size != null ? size : "");
        edtVariantColor.setText(color != null ? color : "");
        edtVariantPrice.setText(price != 0.0 ? String.valueOf(price) : "");
        edtVariantStock.setText(stock != 0 ? String.valueOf(stock) : "");

        btnRemoveVariant.setOnClickListener(v -> variantsContainer.removeView(variantView));

        variantsContainer.addView(variantView);
    }

    private void updateProduct() {
        try {
            String name = edtName.getText().toString().trim();
            String description = edtDescription.getText().toString().trim();
            String categoryIdStr = edtCategoryId.getText().toString().trim();
            Integer categoryId = categoryIdStr.isEmpty() ? null : Integer.parseInt(categoryIdStr);
            String brand = edtBrand.getText().toString().trim();
            double basePrice = Double.parseDouble(edtBasePrice.getText().toString().trim());
            Integer status = Integer.parseInt(edtStatus.getText().toString().trim());
            String imageUrl = edtImageUrl.getText().toString().trim();
            boolean isDelete = chkIsDelete.isChecked();

            Map<String, List<String>> availableAttributesMap = new HashMap<>();
            if (!edtAvailableSizes.getText().toString().trim().isEmpty()) {
                availableAttributesMap.put("size", Arrays.asList(edtAvailableSizes.getText().toString().trim().split("[,.]")));
            }
            if (!edtAvailableColors.getText().toString().trim().isEmpty()) {
                availableAttributesMap.put("color", Arrays.asList(edtAvailableColors.getText().toString().trim().split("[,.]")));
            }
            String availableAttributesJson = gson.toJson(availableAttributesMap);

            List<SaleUpdateProductVariantDto> variantsList = new ArrayList<>();
            for (int i = 0; i < variantsContainer.getChildCount(); i++) {
                View variantView = variantsContainer.getChildAt(i);
                String size = ((EditText) variantView.findViewById(R.id.edtVariantSize)).getText().toString().trim();
                String color = ((EditText) variantView.findViewById(R.id.edtVariantColor)).getText().toString().trim();
                String priceStr = ((EditText) variantView.findViewById(R.id.edtVariantPrice)).getText().toString().trim();
                String stockStr = ((EditText) variantView.findViewById(R.id.edtVariantStock)).getText().toString().trim();

                JsonObject attributesObject = new JsonObject();
                if (!size.isEmpty()) attributesObject.addProperty("size", size);
                if (!color.isEmpty()) attributesObject.addProperty("color", color);

                JsonObject variantDetailsObject = new JsonObject();
                if (!priceStr.isEmpty()) variantDetailsObject.addProperty("price", Double.parseDouble(priceStr));
                if (!stockStr.isEmpty()) variantDetailsObject.addProperty("stock", Integer.parseInt(stockStr));

                String attributesJsonForVariant = gson.toJson(attributesObject);
                String variantsJsonForVariant = gson.toJson(variantDetailsObject);

                int variantId = i < variantIds.size() ? variantIds.get(i) : 0;
                variantsList.add(new SaleUpdateProductVariantDto(variantId, attributesJsonForVariant, variantsJsonForVariant));
            }

            List<SaleUpdateProductImageDto> images = new ArrayList<>();
            if (!imageUrl.isEmpty()) {
                images.add(new SaleUpdateProductImageDto(productImageId, imageUrl));
            }


            SaleUpdateProductDto productDto = new SaleUpdateProductDto(
                    name, description, categoryId, brand, basePrice,
                    availableAttributesJson, status, isDelete,
                    images, variantsList
            );


            Call<SaleProductResponseDto> call = saleApiService.updateProduct(productIdToEdit, productDto);
            call.enqueue(new Callback<SaleProductResponseDto>() {
                @Override
                public void onResponse(Call<SaleProductResponseDto> call, Response<SaleProductResponseDto> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Toast.makeText(SaleEditProductActivity.this, "Cập nhật sản phẩm thành công!", Toast.LENGTH_SHORT).show();
                        setResult(RESULT_OK);
                        finish();
                    } else {
                        Toast.makeText(SaleEditProductActivity.this, "Lỗi khi cập nhật sản phẩm!", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<SaleProductResponseDto> call, Throwable t) {
                    Toast.makeText(SaleEditProductActivity.this, "Không thể kết nối đến máy chủ!", Toast.LENGTH_LONG).show();
                }
            });
        } catch (Exception e) {
            Toast.makeText(this, "Dữ liệu nhập không hợp lệ: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
