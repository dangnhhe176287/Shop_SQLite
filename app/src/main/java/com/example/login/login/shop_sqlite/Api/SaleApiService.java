package com.example.login.login.shop_sqlite.Api;

import com.example.login.login.shop_sqlite.Dto.SaleCreateOrderDto;
import com.example.login.login.shop_sqlite.Dto.SaleCreateProductCategoryDto;
import com.example.login.login.shop_sqlite.Dto.SaleCreateProductDto;
import com.example.login.login.shop_sqlite.Dto.SaleOrderResponseDto;
import com.example.login.login.shop_sqlite.Dto.SaleProductCategoryResponseDto;
import com.example.login.login.shop_sqlite.Dto.SaleProductResponseDto; // Đảm bảo import này là ProductResponseDto, không phải ProductDetailResponseDto
import com.example.login.login.shop_sqlite.Dto.SaleUpdateOrderDto;
import com.example.login.login.shop_sqlite.Dto.SaleUpdateProductCategoryDto;
import com.example.login.login.shop_sqlite.Dto.SaleUpdateProductDto;
import com.example.login.login.shop_sqlite.Dto.SaleUserCreateDto;
import com.example.login.login.shop_sqlite.Dto.SaleUserResponseDto;
import com.example.login.login.shop_sqlite.Models.LoginRequestDto;
import com.example.login.login.shop_sqlite.Models.LoginResponseDto;
import com.example.login.login.shop_sqlite.Models.SaleOrder;
import com.example.login.login.shop_sqlite.Models.RegisterRequestDto;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface SaleApiService {
    @POST("Auth/login")
    Call<LoginResponseDto> login(@Body LoginRequestDto loginRequest);

    @POST("Auth/register")
    Call<LoginResponseDto> register(@Body RegisterRequestDto registerRequest);

    @GET("SaleOrder")
    Call<List<SaleOrder>> getAllOrders();

    @GET("SaleOrder/{id}")
    Call<SaleOrder> getOrderById(@Path("id") int id);

    @POST("SaleOrder")
    Call<SaleOrder> createOrder(@Body SaleCreateOrderDto orderDto);

    @PUT("SaleOrder/{id}")
    //Call<SaleOrder> updateOrder(@Path("id") int orderId, @Body SaleUpdateOrderDto saleUpdateOrderDto);
    Call<SaleOrderResponseDto> updateOrder(@Path("id") int id, @Body SaleUpdateOrderDto orderDto);


    @DELETE("SaleOrder/{id}")
    Call<Void> deleteOrder(@Path("id") int id);

    @GET("SaleProduct/categories")
    Call<List<SaleProductCategoryResponseDto>> getAllProductCategories();

    @POST("SaleProduct/categories")
    Call<SaleProductCategoryResponseDto> createCategory(@Body SaleCreateProductCategoryDto categoryDto);

    @PUT("SaleProduct/categories/{id}")
    Call<SaleProductCategoryResponseDto> updateCategory(
            @Path("id") int id,
            @Body SaleUpdateProductCategoryDto categoryDto
    );
    @DELETE("SaleProduct/categories/{id}")
    Call<Void> deleteCategory(@Path("id") int id);

    @GET("SaleProduct/products")
    Call<List<SaleProductResponseDto>> getAllProducts();

    @POST("SaleProduct/products")
    Call<SaleProductResponseDto> createProduct(@Body SaleCreateProductDto productDto);

    @GET("SaleProduct/products/{id}")
    Call<SaleProductResponseDto> getProductById(@Path("id") int productId);

    @PUT("SaleProduct/products/{id}")
    Call<SaleProductResponseDto> updateProduct(@Path("id") int id, @Body SaleUpdateProductDto productDto);
    @DELETE("SaleProduct/products/{id}")
    Call<Void> deleteProduct(@Path("id") int productId);
    @GET("SaleProduct/categories/{id}")
    Call<SaleProductCategoryResponseDto> getProductCategoryById(@Path("id") int categoryId);

    @GET("SaleUser")
    Call<List<SaleUserResponseDto>> getAllUsers();
    @POST("SaleUser")
    Call<SaleUserResponseDto> createUser(@Body SaleUserCreateDto saleUserCreateDto);
    @GET("SaleUser/{id}")
    Call<SaleUserResponseDto> getUserById(@Path("id") int id);

    @PUT("SaleUser/{id}")
    Call<Void> updateUser(@Path("id") int id, @Body SaleUserResponseDto userDto);

    @DELETE("SaleUser/{id}")
    Call<Void> deleteUser(@Path("id") int id);

}