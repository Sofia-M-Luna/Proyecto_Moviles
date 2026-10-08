package com.example.dsy1105_006d_lc7.data.remote.api

import com.example.dsy1105_006d_lc7.data.remote.dto.ProductDto
import retrofit2.Response
import retrofit2.http.*

/**
 * Servicio API REST para sincronización background con un servidor remoto (ficticio para el MVP).
 * En producción se conectaría a un backend real para sincronizar inventario.
 */
interface ApiService {

    @GET("api/products")
    suspend fun getProducts(): Response<List<ProductDto>>

    @GET("api/products/{id}")
    suspend fun getProductById(@Path("id") id: Long): Response<ProductDto>

    @POST("api/products")
    suspend fun createProduct(@Body product: ProductDto): Response<ProductDto>

    @PUT("api/products/{id}")
    suspend fun updateProduct(@Path("id") id: Long, @Body product: ProductDto): Response<ProductDto>

    @DELETE("api/products/{id}")
    suspend fun deleteProduct(@Path("id") id: Long): Response<Unit>

    @POST("api/sync/products")
    suspend fun syncProducts(@Body products: List<ProductDto>): Response<List<ProductDto>>
}
