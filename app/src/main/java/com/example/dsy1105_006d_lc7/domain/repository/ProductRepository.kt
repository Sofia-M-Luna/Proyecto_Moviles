package com.example.dsy1105_006d_lc7.domain.repository

import com.example.dsy1105_006d_lc7.domain.model.Product
import kotlinx.coroutines.flow.Flow

/**
 * Interfaz de repositorio para productos (Domain Layer).
 * La implementación concreta reside en la Data Layer.
 */
interface ProductRepository {
    fun getAllProducts(): Flow<List<Product>>
    fun searchProducts(query: String): Flow<List<Product>>
    fun getLowStockProducts(): Flow<List<Product>>
    fun getProductsByCategory(category: String): Flow<List<Product>>
    fun getProductsBySupplierId(supplierId: Long): Flow<List<Product>>
    fun getAllCategories(): Flow<List<String>>
    fun getProductCount(): Flow<Int>
    fun getLowStockCount(): Flow<Int>
    fun getOutOfStockCount(): Flow<Int>
    suspend fun getProductById(productId: Long): Product?
    suspend fun getProductByCode(code: String): Product?
    suspend fun insertProduct(product: Product): Long
    suspend fun updateProduct(product: Product)
    suspend fun deleteProduct(product: Product)
    suspend fun updateStock(productId: Long, newStock: Int)
}
