package com.example.login.login.shop_sqlite.Api;

import com.example.login.login.shop_sqlite.Dto.CreateOrderDto;
import com.example.login.login.shop_sqlite.Dto.CreateProductCategoryDto;
import com.example.login.login.shop_sqlite.Dto.CreateProductDto;
import com.example.login.login.shop_sqlite.Dto.ProductCategoryResponseDto;
import com.example.login.login.shop_sqlite.Dto.ProductResponseDto; // Đảm bảo import này là ProductResponseDto, không phải ProductDetailResponseDto
import com.example.login.login.shop_sqlite.Dto.UpdateOrderDto;
import com.example.login.login.shop_sqlite.Dto.UpdateProductCategoryDto;
import com.example.login.login.shop_sqlite.Models.LoginRequestDto;
import com.example.login.login.shop_sqlite.Models.LoginResponseDto;
import com.example.login.login.shop_sqlite.Models.Order;
import com.example.login.login.shop_sqlite.Models.RegisterRequestDto;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ApiService {
    @POST("api/Auth/login")
    Call<LoginResponseDto> login(@Body LoginRequestDto loginRequest);

    @POST("api/Auth/register")
    Call<LoginResponseDto> register(@Body RegisterRequestDto registerRequest);

    @GET("SaleOrder")
    Call<List<Order>> getAllOrders();

    @GET("SaleOrder/{id}")
    Call<Order> getOrderById(@Path("id") int id);

    @POST("SaleOrder")
    Call<Order> createOrder(@Body CreateOrderDto orderDto);

    @PUT("SaleOrder/{id}")
    Call<Order> updateOrder(@Path("id") int orderId, @Body UpdateOrderDto updateOrderDto);

    @DELETE("SaleOrder/{id}")
    Call<Void> deleteOrder(@Path("id") int id);

    @GET("SaleProduct/categories")
    Call<List<ProductCategoryResponseDto>> getAllProductCategories();

    @POST("SaleProduct/categories")
    Call<ProductCategoryResponseDto> createCategory(@Body CreateProductCategoryDto categoryDto);

    @PUT("SaleProduct/categories/{id}")
    Call<ProductCategoryResponseDto> updateCategory(
            @Path("id") int id,
            @Body UpdateProductCategoryDto categoryDto
    );
    @DELETE("SaleProduct/categories/{id}")
    Call<Void> deleteCategory(@Path("id") int id);

    @GET("SaleProduct/products")
    Call<List<ProductResponseDto>> getAllProducts();

    @POST("SaleProduct/products")
    Call<ProductResponseDto> createProduct(@Body CreateProductDto productDto);

    @GET("SaleProduct/products/{id}")
    Call<ProductResponseDto> getProductById(@Path("id") int productId);

    @PUT("SaleProduct/products/{id}")
    Call<ProductResponseDto> updateProduct(@Path("id") int productId, @Body CreateProductDto productDto);
    @DELETE("SaleProduct/products/{id}")
    Call<Void> deleteProduct(@Path("id") int productId);
    @GET("SaleProduct/categories/{id}")
    Call<ProductCategoryResponseDto> getProductCategoryById(@Path("id") int categoryId);

}