package com.example.login.login.shop_sqlite.Activity;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.login.login.shop_sqlite.R;
import com.example.login.login.shop_sqlite.Models.OrderRequest;
import com.example.login.login.shop_sqlite.Models.CartItemDto;
import com.example.login.login.shop_sqlite.Models.CartResponse;
import com.example.login.login.shop_sqlite.Api.ApiService;
import com.example.login.login.shop_sqlite.Api.ApiClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.ArrayList;
import java.util.List;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import com.google.gson.annotations.SerializedName;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Header;
import retrofit2.http.Body;
import java.util.Map;
import java.util.HashMap;
import android.util.Log;
import retrofit2.http.Query;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.view.View;

import android.content.Intent;

// GHN Models
class Province {
    @SerializedName("ProvinceID") public int provinceId;
    @SerializedName("ProvinceName") public String provinceName;
}
class ProvinceResponse { @SerializedName("data") public java.util.List<Province> data; }
class District {
    @SerializedName("DistrictID") public int districtId;
    @SerializedName("DistrictName") public String districtName;
}
class DistrictResponse { @SerializedName("data") public java.util.List<District> data; }
class Ward {
    @SerializedName("WardCode") public String wardCode;
    @SerializedName("WardName") public String wardName;
}
class WardResponse { @SerializedName("data") public java.util.List<Ward> data; }

// GHN API
interface GHNApiService {
    @GET("shiip/public-api/master-data/province")
    Call<ProvinceResponse> getProvinces(@Header("Token") String token);
    @POST("shiip/public-api/master-data/district")
    Call<DistrictResponse> getDistricts(@Header("Token") String token, @Body Map<String, Integer> body);
    @GET("shiip/public-api/master-data/ward")
    Call<WardResponse> getWards(@Header("Token") String token, @Query("district_id") int districtId);
    @POST("shiip/public-api/v2/shipping-order/fee")
    Call<FeeResponse> calculateFee(
        @Header("Token") String token,
        @Header("ShopId") int shopId,
        @Body FeeRequest request
    );
    @POST("shiip/public-api/v2/shipping-order/available-services")
    Call<AvailableServiceResponse> getAvailableServices(
        @Header("Token") String token,
        @Body AvailableServiceRequest request
    );
}

// Thêm model cho FeeRequest và FeeResponse
class FeeRequest {
    @SerializedName("from_district_id") public int fromDistrictId;
    @SerializedName("from_ward_code") public String fromWardCode;
    @SerializedName("service_id") public int serviceId;
    @SerializedName("to_district_id") public int toDistrictId;
    @SerializedName("to_ward_code") public String toWardCode;
    @SerializedName("height") public int height;
    @SerializedName("length") public int length;
    @SerializedName("weight") public int weight;
    @SerializedName("width") public int width;
    @SerializedName("insurance_value") public int insuranceValue;
    @SerializedName("items") public java.util.List<Item> items;
    public static class Item {
        @SerializedName("name") public String name;
        @SerializedName("quantity") public int quantity;
        @SerializedName("height") public int height;
        @SerializedName("weight") public int weight;
        @SerializedName("length") public int length;
        @SerializedName("width") public int width;
    }
}
class FeeResponse {
    @SerializedName("code") public int code;
    @SerializedName("message") public String message;
    @SerializedName("data") public FeeData data;
    public static class FeeData {
        @SerializedName("total") public int total;
        @SerializedName("service_fee") public int serviceFee;
    }
}

// Thêm model cho AvailableServiceRequest và AvailableServiceResponse
class AvailableServiceRequest {
    @SerializedName("shop_id") public int shopId;
    @SerializedName("from_district") public int fromDistrict;
    @SerializedName("to_district") public int toDistrict;
}
class AvailableServiceResponse {
    @SerializedName("code") public int code;
    @SerializedName("message") public String message;
    @SerializedName("data") public java.util.List<ServiceData> data;
    public static class ServiceData {
        @SerializedName("service_id") public int serviceId;
        @SerializedName("short_name") public String shortName;
        @SerializedName("service_type_id") public int serviceTypeId;
    }
}

public class OrderActivity extends AppCompatActivity {
    private Spinner spinnerProvince, spinnerDistrict, spinnerWard;
    private ArrayAdapter<String> provinceAdapter, districtAdapter, wardAdapter;
    private java.util.List<Province> provinceList = new java.util.ArrayList<>();
    private java.util.List<District> districtList = new java.util.ArrayList<>();
    private java.util.List<Ward> wardList = new java.util.ArrayList<>();
    private String ghnToken = "8e05f798-fd7e-11ef-b664-663e10299751"; // <-- Thay bằng token thật
    private GHNApiService ghnApi;
    private LinearLayout cartInfoLayout;
    private TextView tvCartItems, tvCartTotal;
    private int shopId = 196127;
    private int serviceId = 1;
    private int fromDistrictId = 2260; // cập nhật id thực tế của shop
    private String fromWardCode = "541108"; // cập nhật code thực tế của shop
    private List<CartItemDto> cartItems = new ArrayList<>();
    private EditText etOrderNote;
    private Button btnPlaceOrder;
    private double shippingFee = 0; // Thêm biến lưu phí ship

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        // Không cần findViewById cho etName, etPhone, etAddress nữa
        // etName = findViewById(R.id.etName);
        // etPhone = findViewById(R.id.etPhone);
        // etAddress = findViewById(R.id.etAddress);
        btnPlaceOrder = findViewById(R.id.btnPlaceOrder);
        etOrderNote = findViewById(R.id.etOrderNote);

        spinnerProvince = findViewById(R.id.spinnerProvince);
        spinnerDistrict = findViewById(R.id.spinnerDistrict);
        spinnerWard = findViewById(R.id.spinnerWard);
        provinceAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new java.util.ArrayList<>());
        provinceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerProvince.setAdapter(provinceAdapter);
        districtAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new java.util.ArrayList<>());
        districtAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDistrict.setAdapter(districtAdapter);
        wardAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new java.util.ArrayList<>());
        wardAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerWard.setAdapter(wardAdapter);
        // Init Retrofit GHN
        Retrofit ghnRetrofit = new Retrofit.Builder()
            .baseUrl("https://dev-online-gateway.ghn.vn/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();
        ghnApi = ghnRetrofit.create(GHNApiService.class);
        // Load provinces
        loadProvinces();
        spinnerProvince.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(android.widget.AdapterView<?> parent, android.view.View view, int position, long id) {
                if (provinceList.size() > position) {
                    int provinceId = provinceList.get(position).provinceId;
                    loadDistricts(provinceId);
                }
            }
            @Override public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });
        spinnerDistrict.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(android.widget.AdapterView<?> parent, android.view.View view, int position, long id) {
                if (districtList.size() > position) {
                    int districtId = districtList.get(position).districtId;
                    loadWards(districtId);
                }
            }
            @Override public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });
        spinnerWard.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(android.widget.AdapterView<?> parent, android.view.View view, int position, long id) {
                if (wardList.size() > position) {
                    int toDistrictId = districtList.get(spinnerDistrict.getSelectedItemPosition()).districtId;
                    String toWardCode = wardList.get(position).wardCode;
                    // Gọi API lấy available services
                    AvailableServiceRequest serviceRequest = new AvailableServiceRequest();
                    serviceRequest.shopId = shopId;
                    serviceRequest.fromDistrict = fromDistrictId;
                    serviceRequest.toDistrict = toDistrictId;
                    ghnApi.getAvailableServices(ghnToken, serviceRequest).enqueue(new Callback<AvailableServiceResponse>() {
                        @Override
                        public void onResponse(Call<AvailableServiceResponse> call, Response<AvailableServiceResponse> response) {
                            if (response.isSuccessful() && response.body() != null && response.body().data != null && !response.body().data.isEmpty()) {
                                int serviceId = response.body().data.get(0).serviceId; // lấy dịch vụ đầu tiên
                                // Gọi tiếp API calculateFee với serviceId này
                                FeeRequest feeRequest = new FeeRequest();
                                feeRequest.fromDistrictId = fromDistrictId;
                                feeRequest.fromWardCode = fromWardCode;
                                feeRequest.serviceId = serviceId;
                                feeRequest.toDistrictId = toDistrictId;
                                feeRequest.toWardCode = toWardCode;
                                feeRequest.height = 10;
                                feeRequest.length = 20;
                                feeRequest.weight = 0; // sẽ tính tổng bên dưới
                                feeRequest.width = 20;
                                feeRequest.insuranceValue = 0;
                                // Lấy cartItems từ biến toàn cục hoặc cache lại khi load cart
                                java.util.List<FeeRequest.Item> feeItems = new java.util.ArrayList<>();
                                int totalWeight = 0;
                                for (CartItemDto item : cartItems) {
                                    FeeRequest.Item feeItem = new FeeRequest.Item();
                                    feeItem.name = item.getProductName();
                                    feeItem.quantity = item.getQuantity();
                                    feeItem.height = 10; // hoặc lấy từ item nếu có
                                    feeItem.length = 20;
                                    feeItem.width = 20;
                                    feeItem.weight = 1000; // hoặc lấy từ item nếu có
                                    totalWeight += feeItem.weight * feeItem.quantity;
                                    feeItems.add(feeItem);
                                }
                                feeRequest.items = feeItems;
                                feeRequest.weight = totalWeight;
                                Log.d("GHN", "FeeRequest JSON: " + new com.google.gson.Gson().toJson(feeRequest));
                                ghnApi.calculateFee(ghnToken, shopId, feeRequest).enqueue(new Callback<FeeResponse>() {
                                    @Override
                                    public void onResponse(Call<FeeResponse> call, Response<FeeResponse> response) {
                                        if (response.isSuccessful() && response.body() != null && response.body().data != null) {
                                            int totalFeeVND = response.body().data.total;
                                            double totalFeeUSD = totalFeeVND / 25000.0; // Quy đổi sang USD
                                            shippingFee = totalFeeUSD; // Lưu phí ship
                                            // Lấy tổng tiền hàng (USD) từ tvCartTotal (đã set trước đó)
                                            String totalText = tvCartTotal.getText().toString();
                                            double totalOrderUSD = 0;
                                            try {
                                                // Giả sử dòng đầu là "Tổng tiền: $xx.xx"
                                                String[] lines = totalText.split("\\n");
                                                for (String line : lines) {
                                                    if (line.startsWith("Tổng tiền: $")) {
                                                        totalOrderUSD = Double.parseDouble(line.replace("Tổng tiền: $", "").trim());
                                                        break;
                                                    }
                                                }
                                            } catch (Exception e) { totalOrderUSD = 0; }
                                            double grandTotal = totalOrderUSD + totalFeeUSD;
                                            tvCartTotal.setText(String.format("Tổng tiền: $%.4f\nPhí ship: $%.4f\nTổng cộng: $%.4f", totalOrderUSD, totalFeeUSD, grandTotal));
                                        } else {
                                            String errorMsg = "";
                                            if (response.errorBody() != null) {
                                                try { errorMsg = response.errorBody().string(); } catch (Exception e) { errorMsg = "errorBody parse failed: " + e.getMessage(); }
                                            }
                                            Log.e("GHN", "Fee API error: code=" + response.code() + ", body=" + (response.body() != null ? new com.google.gson.Gson().toJson(response.body()) : "null") + ", errorBody=" + errorMsg);
                                            tvCartTotal.setText(tvCartTotal.getText() + "\nKhông lấy được phí ship!");
                                        }
                                    }
                                    @Override
                                    public void onFailure(Call<FeeResponse> call, Throwable t) {
                                        Log.e("GHN", "Fee API failure: " + t.getMessage(), t);
                                    }
                                });
                            } else {
                                Log.e("GHN", "No available service for this route! code=" + response.code());
                                tvCartTotal.setText(tvCartTotal.getText() + "\nKhông có dịch vụ GHN cho tuyến này!");
                            }
                        }
                        @Override
                        public void onFailure(Call<AvailableServiceResponse> call, Throwable t) {
                            Log.e("GHN", "Available service API failure: " + t.getMessage(), t);
                            tvCartTotal.setText(tvCartTotal.getText() + "\nLỗi lấy dịch vụ GHN!");
                        }
                    });
                }
            }
            @Override public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });

        // Ẩn các trường nhập thông tin khách hàng
        // etName.setVisibility(View.GONE);
        // etPhone.setVisibility(View.GONE);
        // etAddress.setVisibility(View.GONE);
        // Hiển thị thông tin giỏ hàng
        cartInfoLayout = new LinearLayout(this);
        cartInfoLayout.setOrientation(LinearLayout.VERTICAL);
        tvCartItems = new TextView(this);
        tvCartTotal = new TextView(this);
        cartInfoLayout.addView(tvCartItems);
        cartInfoLayout.addView(tvCartTotal);
        ((LinearLayout) findViewById(R.id.orderRootLayout)).addView(cartInfoLayout, 0);
        // Lấy userId từ SharedPreferences
        android.content.SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        int userId = prefs.getInt("current_user_id", 0);
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        apiService.getCart(userId).enqueue(new Callback<CartResponse>() {
            @Override
            public void onResponse(Call<CartResponse> call, Response<CartResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    cartItems = response.body().getItems();
                    double total = response.body().getAmountDue();
                    StringBuilder sb = new StringBuilder();
                    for (CartItemDto item : cartItems) {
                        sb.append("• ")
                          .append(item.getProductName())
                          .append("  x")
                          .append(item.getQuantity())
                          .append("  |  Đơn giá: $")
                          .append(String.format("%.2f", item.getPrice() != null ? item.getPrice() : 0))
                          .append("  |  Thành tiền: $")
                          .append(String.format("%.2f", (item.getPrice() != null ? item.getPrice() : 0) * item.getQuantity()))
                          .append("\n");
                    }
                    tvCartItems.setText("Sản phẩm trong giỏ hàng:\n" + sb.toString());
                    tvCartItems.setTextSize(16);
                    tvCartTotal.setText("Tổng tiền: $" + String.format("%.2f", total));
                    tvCartTotal.setTextSize(18);
                    tvCartTotal.setPadding(0, 16, 0, 0);
                } else {
                    tvCartItems.setText("Không lấy được giỏ hàng!");
                }
            }
            @Override
            public void onFailure(Call<CartResponse> call, Throwable t) {
                tvCartItems.setText("Lỗi lấy giỏ hàng!");
            }
        });
        // Ẩn nút đặt hàng hoặc có thể disable
        // btnPlaceOrder.setVisibility(View.GONE); // Đã xóa dòng này để nút luôn hiển thị

        // Không cần lấy giá trị name, phone, detailAddress từ EditText nữa
        // String name = etName.getText().toString().trim();
        // String phone = etPhone.getText().toString().trim();
        // String detailAddress = etAddress.getText().toString().trim();
        // String provinceName = spinnerProvince.getSelectedItem() != null ? spinnerProvince.getSelectedItem().toString() : "";
        // String districtName = spinnerDistrict.getSelectedItem() != null ? spinnerDistrict.getSelectedItem().toString() : "";
        // String wardName = spinnerWard.getSelectedItem() != null ? spinnerWard.getSelectedItem().toString() : "";
        // String orderNote = etOrderNote.getText().toString().trim();

        // Ghép địa chỉ hoàn chỉnh
        // String shippingAddress = detailAddress + ", " + wardName + ", " + districtName + ", " + provinceName;

        // Kiểm tra thông tin
        // if (name.isEmpty() || phone.isEmpty() || detailAddress.isEmpty() || provinceName.isEmpty() || districtName.isEmpty() || wardName.isEmpty()) {
        //     Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
        //     return;
        // }
        // KHÔNG khai báo lại prefs, userId, apiService ở đây nữa, chỉ dùng lại biến đã khai báo ở trên
        // apiService.getCart(userId).enqueue(new Callback<CartResponse>() {
        //     @Override
        //     public void onResponse(Call<CartResponse> call, Response<CartResponse> response) {
        //         if (response.isSuccessful() && response.body() != null) {
        //             List<CartItemDto> cartItems = response.body().getItems();
        //             double total = response.body().getAmountDue();
        //             List<OrderRequest.OrderDetail> orderDetails = new ArrayList<>();
        //             for (CartItemDto item : cartItems) {
        //                 orderDetails.add(new OrderRequest.OrderDetail(
        //                     item.getProductId(),
        //                     item.getProductName(),
        //                     item.getPrice() != null ? item.getPrice() : 0,
        //                     item.getQuantity()
        //                 ));
        //             }
        //             // Tạo đơn hàng với shippingAddress
        //             OrderRequest orderRequest = new OrderRequest(userId, name, phone, detailAddress, total, orderDetails, shippingAddress, orderNote);
        //             apiService.placeOrder(orderRequest).enqueue(new Callback<Void>() {
        //                 @Override
        //                 public void onResponse(Call<Void> call, Response<Void> response) {
        //                     Toast.makeText(OrderActivity.this, "Đặt hàng thành công!", Toast.LENGTH_SHORT).show();
        //                     finish();
        //                 }
        //                 @Override
        //                 public void onFailure(Call<Void> call, Throwable t) {
        //                     Toast.makeText(OrderActivity.this, "Lỗi đặt hàng!", Toast.LENGTH_SHORT).show();
        //                 }
        //             });
        //         } else {
        //             Toast.makeText(OrderActivity.this, "Không lấy được giỏ hàng!", Toast.LENGTH_SHORT).show();
        //         }
        //     }
        //     @Override
        //     public void onFailure(Call<CartResponse> call, Throwable t) {
        //         Toast.makeText(OrderActivity.this, "Lỗi lấy giỏ hàng!", Toast.LENGTH_SHORT).show();
        //     }
        // });

        // Không cần kiểm tra name, phone, detailAddress nữa
        btnPlaceOrder.setOnClickListener(v -> {
            // String name = etName.getText().toString().trim();
            // String phone = etPhone.getText().toString().trim();
            // String detailAddress = etAddress.getText().toString().trim();
            String provinceName = spinnerProvince.getSelectedItem() != null ? spinnerProvince.getSelectedItem().toString() : "";
            String districtName = spinnerDistrict.getSelectedItem() != null ? spinnerDistrict.getSelectedItem().toString() : "";
            String wardName = spinnerWard.getSelectedItem() != null ? spinnerWard.getSelectedItem().toString() : "";
            String orderNote = etOrderNote.getText().toString().trim();

            // Chỉ cho đặt hàng khi đã chọn đủ cả 3 trường địa chỉ
            if (provinceName.isEmpty() || districtName.isEmpty() || wardName.isEmpty() || provinceName.equals("Chọn tỉnh") || districtName.equals("Chọn huyện") || wardName.equals("Chọn xã")) {
                Toast.makeText(this, "Vui lòng chọn đầy đủ Tỉnh/Thành, Quận/Huyện, Phường/Xã trước khi đặt hàng!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Ghép địa chỉ hoàn chỉnh
            String shippingAddress = wardName + ", " + districtName + ", " + provinceName;

            // Kiểm tra thông tin
            // if (name.isEmpty() || phone.isEmpty() || detailAddress.isEmpty() || provinceName.isEmpty() || districtName.isEmpty() || wardName.isEmpty()) {
            //     Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
            //     return;
            // }
            // KHÔNG khai báo lại prefs, userId, apiService ở đây nữa, chỉ dùng lại biến đã khai báo ở trên
            apiService.getCart(userId).enqueue(new Callback<CartResponse>() {
                @Override
                public void onResponse(Call<CartResponse> call, Response<CartResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        List<CartItemDto> cartItems = response.body().getItems();
                        double total = response.body().getAmountDue();
                        List<OrderRequest.OrderDetail> orderDetails = new ArrayList<>();
                        for (CartItemDto item : cartItems) {
                            orderDetails.add(new OrderRequest.OrderDetail(
                                item.getProductId(),
                                item.getProductName(),
                                item.getPrice() != null ? item.getPrice() : 0,
                                item.getQuantity(),
                                item.getVariantId(),
                                item.getVariantAttributes()
                            ));
                        }
                        // Tạo đơn hàng với shippingAddress
                        OrderRequest orderRequest = new OrderRequest(userId, "", "", "", total, shippingFee, orderDetails, shippingAddress, orderNote);
                        apiService.placeOrder(orderRequest).enqueue(new Callback<Void>() {
                            @Override
                            public void onResponse(Call<Void> call, Response<Void> response) {
                                Log.d("OrderActivity", "Place order response: " + response.code());
                                if (response.isSuccessful()) {

                                    Log.d("OrderActivity", "Order placed successfully, clearing cart for user: " + userId);
                                    apiService.clearCart(userId).enqueue(new Callback<Void>() {
                                        @Override
                                        public void onResponse(Call<Void> clearCall, Response<Void> clearResponse) {
                                            Log.d("OrderActivity", "Clear cart response: " + clearResponse.code());
                                          
                                            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(OrderActivity.this);
                                            builder.setTitle("Đặt hàng thành công!")
                                                    .setMessage("Đơn hàng của bạn đã được đặt thành công và giỏ hàng đã được làm sạch.\n\n" +
                                                               "Địa chỉ giao hàng: " + shippingAddress + "\n" +
                                                               "Tổng tiền hàng: $" + String.format("%.2f", total) + "\n" +
                                                               "Phí ship: $" + String.format("%.2f", shippingFee) + "\n" +
                                                               "Tổng cộng: $" + String.format("%.2f", total + shippingFee) + "\n" +
                                                               "Ghi chú: " + (orderNote.isEmpty() ? "Không có" : orderNote))
                                                    .setPositiveButton("Xem đơn hàng", (dialog, which) -> {
                                                        Intent intent = new Intent(OrderActivity.this, OrderListActivity.class);
                                                        startActivity(intent);
                                                        setResult(RESULT_OK);
                                                        finish();
                                                    })
                                                    .setNegativeButton("Về trang chủ", (dialog, which) -> {
                                                        Intent intent = new Intent(OrderActivity.this, ProductListActivity.class);
                                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                        startActivity(intent);
                                                        setResult(RESULT_OK);
                                                        finish();
                                                    })
                                                    .setCancelable(false)
                                                    .show();
                                        }
                                        @Override
                                        public void onFailure(Call<Void> clearCall, Throwable clearT) {
                                            Log.e("OrderActivity", "Clear cart failed: " + clearT.getMessage());
                                            // Vẫn hiển thị thông báo thành công ngay cả khi xóa cart thất bại
                                            Toast.makeText(OrderActivity.this, "Đặt hàng thành công! (Lỗi xóa giỏ hàng)", Toast.LENGTH_LONG).show();
                                            setResult(RESULT_OK);
                                            finish();
                                        }
                                    });
                                } else {
                                    Toast.makeText(OrderActivity.this, "Lỗi đặt hàng: " + response.code(), Toast.LENGTH_SHORT).show();
                                }
                            }
                            @Override
                            public void onFailure(Call<Void> call, Throwable t) {
                                Log.e("OrderActivity", "Place order failed: " + t.getMessage());
                                Toast.makeText(OrderActivity.this, "Lỗi đặt hàng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        Toast.makeText(OrderActivity.this, "Không lấy được giỏ hàng!", Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onFailure(Call<CartResponse> call, Throwable t) {
                    Toast.makeText(OrderActivity.this, "Lỗi lấy giỏ hàng!", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void loadProvinces() {
        ghnApi.getProvinces(ghnToken).enqueue(new Callback<ProvinceResponse>() {
            @Override
            public void onResponse(Call<ProvinceResponse> call, Response<ProvinceResponse> response) {
                Log.d("GHN", "Province API response code: " + response.code());
                if (response.isSuccessful() && response.body() != null) {
                    provinceList = response.body().data;
                    Log.d("GHN", "Provinces: " + new com.google.gson.Gson().toJson(provinceList));
                    java.util.List<String> names = new java.util.ArrayList<>();
                    for (Province p : provinceList) names.add(p.provinceName);
                    provinceAdapter.clear(); provinceAdapter.addAll(names); provinceAdapter.notifyDataSetChanged();
                } else {
                    Log.e("GHN", "Province API error: " + response.message());
                }
            }
            @Override public void onFailure(Call<ProvinceResponse> call, Throwable t) {
                Log.e("GHN", "Province API failure: " + t.getMessage(), t);
            }
        });
    }
    private void loadDistricts(int provinceId) {
        Map<String, Integer> body = new HashMap<>();
        body.put("province_id", provinceId);
        ghnApi.getDistricts(ghnToken, body).enqueue(new Callback<DistrictResponse>() {
            @Override
            public void onResponse(Call<DistrictResponse> call, Response<DistrictResponse> response) {
                Log.d("GHN", "District API response code: " + response.code());
                if (response.isSuccessful() && response.body() != null) {
                    districtList = response.body().data;
                    Log.d("GHN", "Districts: " + new com.google.gson.Gson().toJson(districtList));
                    if (districtList != null) {
                        java.util.List<String> names = new java.util.ArrayList<>();
                        for (District d : districtList) names.add(d.districtName);
                        districtAdapter.clear(); districtAdapter.addAll(names); districtAdapter.notifyDataSetChanged();
                    } else {
                        districtAdapter.clear();
                        districtAdapter.notifyDataSetChanged();
                        Log.e("GHN", "districtList is null!");
                    }
                } else {
                    Log.e("GHN", "District API error: " + response.message());
                }
            }
            @Override public void onFailure(Call<DistrictResponse> call, Throwable t) {
                Log.e("GHN", "District API failure: " + t.getMessage(), t);
            }
        });
    }
    private void loadWards(int districtId) {
        ghnApi.getWards(ghnToken, districtId).enqueue(new Callback<WardResponse>() {
            @Override
            public void onResponse(Call<WardResponse> call, Response<WardResponse> response) {
                Log.d("GHN", "Ward API response code: " + response.code());
                if (response.isSuccessful() && response.body() != null) {
                    wardList = response.body().data;
                    Log.d("GHN", "Wards: " + new com.google.gson.Gson().toJson(wardList));
                    java.util.List<String> names = new java.util.ArrayList<>();
                    for (Ward w : wardList) names.add(w.wardName);
                    wardAdapter.clear(); wardAdapter.addAll(names); wardAdapter.notifyDataSetChanged();
                } else {
                    Log.e("GHN", "Ward API error: " + response.message());
                }
            }
            @Override public void onFailure(Call<WardResponse> call, Throwable t) {
                Log.e("GHN", "Ward API failure: " + t.getMessage(), t);
            }
        });
    }
} 