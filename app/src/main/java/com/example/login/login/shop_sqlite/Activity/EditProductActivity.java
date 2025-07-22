package com.example.login.login.shop_sqlite.Activity;

import android.content.Intent;
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
import com.example.login.login.shop_sqlite.Api.ApiService;
import com.example.login.login.shop_sqlite.Dto.CreateProductDto;
import com.example.login.login.shop_sqlite.Dto.ProductImageDto;
import com.example.login.login.shop_sqlite.Dto.ProductResponseDto;
import com.example.login.login.shop_sqlite.Dto.ProductVariantDto;
import com.example.login.login.shop_sqlite.R;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditProductActivity extends AppCompatActivity {

    private static final String TAG = "EditProductActivity";

    private EditText edtProductId, edtName, edtDescription, edtCategoryId, edtBrand, edtBasePrice, edtStatus, edtImageUrl, edtAvailableSizes, edtAvailableColors;
    private CheckBox chkIsDelete;
    private LinearLayout variantsContainer;
    private Button btnAddVariant, btnUpdate;

    private Gson gson = new Gson();
    private ApiService apiService;
    private int productIdToEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_product);

        initViews();
        apiService = ApiClient.getClient().create(ApiService.class);

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

        btnAddVariant.setOnClickListener(v -> addVariantField(null, null, 0.0, 0)); // Khi thêm mới, giá trị mặc định là rỗng/0
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
        apiService.saleGetProductById(productId).enqueue(new Callback<ProductResponseDto>() {
            @Override
            public void onResponse(Call<ProductResponseDto> call, Response<ProductResponseDto> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ProductResponseDto product = response.body();
                    displayProductDetails(product);
                    Log.i(TAG, "Đã lấy chi tiết sản phẩm thành công.");
                } else {
                    String errorMsg = "Lỗi khi lấy chi tiết sản phẩm: " + response.code();
                    try {
                        if (response.errorBody() != null) {
                            errorMsg += " - Chi tiết: " + response.errorBody().string();
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Lỗi đọc errorBody: " + e.getMessage(), e);
                    }
                    Toast.makeText(EditProductActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                    Log.e(TAG, errorMsg);
                    finish();
                }
            }

            @Override
            public void onFailure(Call<ProductResponseDto> call, Throwable t) {
                String networkError = "Lỗi kết nối khi lấy chi tiết sản phẩm: " + t.getMessage();
                Toast.makeText(EditProductActivity.this, networkError, Toast.LENGTH_LONG).show();
                Log.e(TAG, networkError, t);
                finish();
            }
        });
    }

    private void displayProductDetails(ProductResponseDto product) {
        edtName.setText(product.getName());
        edtDescription.setText(product.getDescription());
        edtCategoryId.setText(product.getProductCategoryId() != null ? String.valueOf(product.getProductCategoryId()) : "");
        edtBrand.setText(product.getBrand());
        edtBasePrice.setText(String.valueOf(product.getBasePrice()));
        edtStatus.setText(product.getStatus() != null ? String.valueOf(product.getStatus()) : "");
        chkIsDelete.setChecked(product.isDelete());

        if (product.getProductImages() != null && !product.getProductImages().isEmpty()) {
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
                Log.e(TAG, "Lỗi phân tích cú pháp AvailableAttributes: " + e.getMessage(), e);
            }
        }

        variantsContainer.removeAllViews();
        if (product.getVariants() != null && !product.getVariants().isEmpty()) {
            for (ProductVariantDto variantDto : product.getVariants()) {
                String size = "";
                String color = "";
                double price = 0.0;
                int stock = 0;

                if (!variantDto.getAttributes().isEmpty()) {
                    try {
                        Type attributesType = new TypeToken<Map<String, String>>(){}.getType();
                        Map<String, String> attributesMap = gson.fromJson(variantDto.getAttributes(), attributesType);
                        if (attributesMap.containsKey("size")) {
                            size = attributesMap.get("size");
                        }
                        if (attributesMap.containsKey("color")) {
                            color = attributesMap.get("color");
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Lỗi phân tích cú pháp attributes JSON cho biến thể: " + e.getMessage(), e);
                    }
                }

                if (!variantDto.getVariants().isEmpty()) {
                    try {
                        Type variantDetailsType = new TypeToken<Map<String, Object>>(){}.getType();
                        Map<String, Object> variantDetailsMap = gson.fromJson(variantDto.getVariants(), variantDetailsType);
                        if (variantDetailsMap.containsKey("price")) {
                            Object priceObj = variantDetailsMap.get("price");
                            if (priceObj instanceof Double) {
                                price = (Double) priceObj;
                            } else if (priceObj instanceof String) {
                                price = Double.parseDouble((String) priceObj);
                            }
                        }
                        if (variantDetailsMap.containsKey("stock")) {
                            Object stockObj = variantDetailsMap.get("stock");
                            if (stockObj instanceof Double) {
                                stock = ((Double) stockObj).intValue();
                            } else if (stockObj instanceof String) {
                                stock = Integer.parseInt((String) stockObj);
                            } else if (stockObj instanceof Integer) {
                                stock = (Integer) stockObj;
                            }
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Lỗi phân tích cú pháp variants JSON cho biến thể: " + e.getMessage(), e);
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
        Log.d(TAG, "Đã thêm một trường biến thể mới, điền dữ liệu nếu có.");
    }

    private void updateProduct() {
        Log.d(TAG, "Bắt đầu quá trình cập nhật sản phẩm...");
        try {
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

            if (name.isEmpty() || description.isEmpty() || brand.isEmpty() || basePriceStr.isEmpty()) {
                String validationError = "Vui lòng điền đầy đủ các trường bắt buộc (Tên, Mô tả, Thương hiệu, Giá cơ bản).";
                Toast.makeText(this, validationError, Toast.LENGTH_LONG).show();
                Log.e(TAG, "Lỗi xác thực: " + validationError);
                return;
            }

            Map<String, List<String>> availableAttributesMap = new HashMap<>();
            if (!availableSizesInput.isEmpty()) {
                List<String> sizesList = new ArrayList<>(Arrays.asList(availableSizesInput.split("[,.]")));
                sizesList.removeIf(String::isEmpty);
                if (!sizesList.isEmpty()) availableAttributesMap.put("size", sizesList);
            }
            if (!availableColorsInput.isEmpty()) {
                List<String> colorsList = new ArrayList<>(Arrays.asList(availableColorsInput.split("[,.]")));
                colorsList.removeIf(String::isEmpty);
                if (!colorsList.isEmpty()) availableAttributesMap.put("color", colorsList);
            }
            String availableAttributesJson = gson.toJson(availableAttributesMap);
            Log.d(TAG, "JSON của Available Attributes gửi đi: " + availableAttributesJson);


            List<ProductVariantDto> variantsList = new ArrayList<>();
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
                    Log.d(TAG, "Bỏ qua biến thể rỗng tại vị trí: " + i);
                    continue;
                }

                JsonObject attributesObject = new JsonObject();
                if (!size.isEmpty()) attributesObject.addProperty("size", size);
                if (!color.isEmpty()) attributesObject.addProperty("color", color);
                String attributesJsonForVariant = gson.toJson(attributesObject);

                JsonObject variantDetailsObject = new JsonObject();
                try {
                    if (!priceStr.isEmpty()) variantDetailsObject.addProperty("price", Double.parseDouble(priceStr));
                } catch (NumberFormatException e) {
                    Toast.makeText(this, "Giá biến thể không hợp lệ tại vị trí " + (i+1), Toast.LENGTH_LONG).show();
                    Log.e(TAG, "Lỗi định dạng số cho giá biến thể: " + priceStr, e);
                    return;
                }
                try {
                    if (!stockStr.isEmpty()) variantDetailsObject.addProperty("stock", Integer.parseInt(stockStr));
                } catch (NumberFormatException e) {
                    Toast.makeText(this, "Số lượng biến thể không hợp lệ tại vị trí " + (i+1), Toast.LENGTH_LONG).show();
                    Log.e(TAG, "Lỗi định dạng số cho số lượng biến thể: " + stockStr, e);
                    return;
                }
                String variantsJsonForVariant = gson.toJson(variantDetailsObject);

                variantsList.add(new ProductVariantDto(attributesJsonForVariant, variantsJsonForVariant));
                Log.d(TAG, "Đã xử lý biến thể: Attributes='" + attributesJsonForVariant + "', Variants='" + variantsJsonForVariant + "'");
            }
            Log.d(TAG, "Tổng số biến thể đã xử lý: " + variantsList.size());

            List<ProductImageDto> images = imageUrl.isEmpty() ? null :
                    Collections.singletonList(new ProductImageDto(imageUrl));

            CreateProductDto productDto = new CreateProductDto(
                    name, description, categoryId, brand, basePrice, availableAttributesJson, status, isDelete, images, variantsList
            );

            String json = gson.toJson(productDto);
            Log.i(TAG, "JSON request gửi đến API: \n" + json);

            Call<ProductResponseDto> call = apiService.updateProduct(productIdToEdit, productDto);

            call.enqueue(new Callback<ProductResponseDto>() {
                @Override
                public void onResponse(Call<ProductResponseDto> call, Response<ProductResponseDto> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Log.i(TAG, "Cập nhật sản phẩm thành công! ID: " + response.body().getProductId());
                        Toast.makeText(EditProductActivity.this, "Cập nhật sản phẩm thành công!", Toast.LENGTH_SHORT).show();
                        setResult(RESULT_OK);
                        finish();
                    } else {
                        String errorMsg = "Lỗi khi cập nhật sản phẩm: " + response.code();
                        try {
                            if (response.errorBody() != null) {
                                String errorBodyString = response.errorBody().string();
                                errorMsg += " - Chi tiết: " + errorBodyString;
                                Log.e(TAG, "Phản hồi lỗi từ Server: " + errorMsg);
                                Toast.makeText(EditProductActivity.this, "Lỗi server: " + errorBodyString, Toast.LENGTH_LONG).show();
                            } else {
                                Log.e(TAG, "Phản hồi lỗi từ Server: " + errorMsg + " (Không có body lỗi)");
                                Toast.makeText(EditProductActivity.this, "Lỗi server không xác định: " + response.code(), Toast.LENGTH_LONG).show();
                            }
                        } catch (Exception e) {
                            Log.e(TAG, "Lỗi khi đọc errorBody: " + e.getMessage(), e);
                            Toast.makeText(EditProductActivity.this, "Lỗi server, không đọc được chi tiết lỗi.", Toast.LENGTH_LONG).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<ProductResponseDto> call, Throwable t) {
                    String networkError = "Không thể kết nối đến máy chủ: " + t.getMessage();
                    Log.e(TAG, networkError, t);
                    Toast.makeText(EditProductActivity.this, networkError, Toast.LENGTH_LONG).show();
                }
            });
        } catch (NumberFormatException e) {
            String numberFormatError = "Dữ liệu số không hợp lệ. Vui lòng kiểm tra lại Giá cơ bản, ID danh mục, Trạng thái, Giá biến thể, Số lượng biến thể.";
            Log.e(TAG, numberFormatError + ": " + e.getMessage(), e);
            Toast.makeText(this, numberFormatError, Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            String generalError = "Có lỗi không xác định xảy ra: " + e.getMessage();
            Log.e(TAG, generalError, e);
            Toast.makeText(this, generalError, Toast.LENGTH_LONG).show();
        }
    }
}