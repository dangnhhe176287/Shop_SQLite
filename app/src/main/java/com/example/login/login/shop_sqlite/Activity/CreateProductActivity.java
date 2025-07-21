package com.example.login.login.shop_sqlite.Activity;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.example.login.login.shop_sqlite.Api.ApiClient;
import com.example.login.login.shop_sqlite.Api.ApiService;
import com.example.login.login.shop_sqlite.Dto.*;
import com.example.login.login.shop_sqlite.R;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.*;

public class CreateProductActivity extends AppCompatActivity {

    private static final String TAG = "CreateProductActivity";

    private EditText edtName, edtDescription, edtCategoryId, edtBrand, edtBasePrice, edtStatus, edtImageUrl, edtAvailableSizes, edtAvailableColors;
    private CheckBox chkIsDelete;
    private LinearLayout variantsContainer;
    private Button btnAddVariant, btnCreate;
    private Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_product);

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

        addVariantField();

        btnAddVariant.setOnClickListener(v -> addVariantField());
        btnCreate.setOnClickListener(v -> createProduct());
    }

    private void addVariantField() {
        View variantView = LayoutInflater.from(this).inflate(R.layout.variant_item, variantsContainer, false);
        variantsContainer.addView(variantView);
        Log.d(TAG, "Đã thêm một trường biến thể mới.");
    }

    private void createProduct() {
        Log.d(TAG, "Bắt đầu quá trình tạo sản phẩm...");
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
                List<String> sizesList = new ArrayList<>();
                String[] sizeArray = availableSizesInput.split("[,.]");
                for (String s : sizeArray) {
                    String trimmed = s.trim();
                    if (!trimmed.isEmpty()) {
                        sizesList.add(trimmed);
                    }
                }
                if (!sizesList.isEmpty()) {
                    availableAttributesMap.put("size", sizesList);
                }
            }

            if (!availableColorsInput.isEmpty()) {
                List<String> colorsList = new ArrayList<>();
                String[] colorArray = availableColorsInput.split("[,.]");
                for (String c : colorArray) {
                    String trimmed = c.trim();
                    if (!trimmed.isEmpty()) {
                        colorsList.add(trimmed);
                    }
                }
                if (!colorsList.isEmpty()) {
                    availableAttributesMap.put("color", colorsList);
                }
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
                if (!size.isEmpty()) {
                    attributesObject.addProperty("size", size);
                }
                if (!color.isEmpty()) {
                    attributesObject.addProperty("color", color);
                }
                String attributesJsonForVariant = gson.toJson(attributesObject);

                JsonObject variantDetailsObject = new JsonObject();
                double price = 0.0;
                int stock = 0;

                try {
                    if (!priceStr.isEmpty()) {
                        price = Double.parseDouble(priceStr);
                        variantDetailsObject.addProperty("price", price);
                    }
                } catch (NumberFormatException e) {
                    Toast.makeText(this, "Giá biến thể không hợp lệ tại vị trí " + (i+1), Toast.LENGTH_LONG).show();
                    Log.e(TAG, "Lỗi định dạng số cho giá biến thể: " + priceStr, e);
                    return;
                }

                try {
                    if (!stockStr.isEmpty()) {
                        stock = Integer.parseInt(stockStr);
                        variantDetailsObject.addProperty("stock", stock);
                    }
                } catch (NumberFormatException e) {
                    Toast.makeText(this, "Số lượng biến thể không hợp lệ tại vị trí " + (i+1), Toast.LENGTH_LONG).show();
                    Log.e(TAG, "Lỗi định dạng số cho số lượng biến thể: " + stockStr, e);
                    return;
                }

                String variantsJsonForVariant = gson.toJson(variantDetailsObject);

                variantsList.add(new ProductVariantDto(attributesJsonForVariant, variantsJsonForVariant));
                Log.d(TAG, "Đã thêm biến thể: Attributes='" + attributesJsonForVariant + "', Variants='" + variantsJsonForVariant + "'");
            }
            Log.d(TAG, "Tổng số biến thể đã xử lý: " + variantsList.size());

            List<ProductImageDto> images = imageUrl.isEmpty() ? null :
                    Collections.singletonList(new ProductImageDto(imageUrl));
            if (images != null) {
                Log.d(TAG, "URL hình ảnh: " + imageUrl);
            } else {
                Log.d(TAG, "Không có URL hình ảnh.");
            }

            CreateProductDto productDto = new CreateProductDto(
                    name, description, categoryId, brand, basePrice, availableAttributesJson, status, isDelete, images, variantsList
            );

            String json = gson.toJson(productDto);
            Log.i(TAG, "JSON request gửi đến API: \n" + json);

            ApiService apiService = ApiClient.getClient().create(ApiService.class);
            Call<ProductResponseDto> call = apiService.createProduct(productDto);

            call.enqueue(new Callback<ProductResponseDto>() {
                @Override
                public void onResponse(Call<ProductResponseDto> call, Response<ProductResponseDto> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Log.i(TAG, "Tạo sản phẩm thành công! ID: " + response.body().getProductId());
                        Toast.makeText(CreateProductActivity.this, "Tạo sản phẩm thành công!", Toast.LENGTH_SHORT).show();

                        // ĐẢM BẢO THÊM DÒNG NÀY:
                        setResult(RESULT_OK); // Đặt kết quả thành công
                        finish(); // Đóng Activity hiện tại và quay về Activity đã gọi nó
                    } else {
                        String errorMsg = "Lỗi khi tạo sản phẩm: " + response.code();
                        try {
                            if (response.errorBody() != null) {
                                String errorBodyString = response.errorBody().string();
                                errorMsg += " - Chi tiết: " + errorBodyString;
                                Log.e(TAG, "Phản hồi lỗi từ Server: " + errorMsg);
                                Toast.makeText(CreateProductActivity.this, "Lỗi server: " + errorBodyString, Toast.LENGTH_LONG).show();
                            } else {
                                Log.e(TAG, "Phản hồi lỗi từ Server: " + errorMsg + " (Không có body lỗi)");
                                Toast.makeText(CreateProductActivity.this, "Lỗi server không xác định: " + response.code(), Toast.LENGTH_LONG).show();
                            }
                        } catch (Exception e) {
                            Log.e(TAG, "Lỗi khi đọc errorBody: " + e.getMessage(), e);
                            Toast.makeText(CreateProductActivity.this, "Lỗi server, không đọc được chi tiết lỗi.", Toast.LENGTH_LONG).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<ProductResponseDto> call, Throwable t) {
                    String networkError = "Không thể kết nối đến máy chủ: " + t.getMessage();
                    Log.e(TAG, networkError, t);
                    Toast.makeText(CreateProductActivity.this, networkError, Toast.LENGTH_LONG).show();
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