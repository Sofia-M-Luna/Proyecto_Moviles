package com.example.dsy1105_006d_lc7.data.repository

import com.example.dsy1105_006d_lc7.data.local.dao.ProductDao
import com.example.dsy1105_006d_lc7.data.local.entities.ProductStatus
import com.example.dsy1105_006d_lc7.domain.model.Product
import com.example.dsy1105_006d_lc7.domain.repository.ProductRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementación concreta del repositorio de productos.
 * Accede a Room como fuente primaria (Offline-First).
 */
@Singleton
class ProductRepositoryImpl @Inject constructor(
    private val productDao: ProductDao
) : ProductRepository {

    override fun getAllProducts(): Flow<List<Product>> =
        productDao.getAllProducts().map { list -> list.map { it.toDomain() } }

    override fun searchProducts(query: String): Flow<List<Product>> =
        productDao.searchProducts(query).map { list -> list.map { it.toDomain() } }

    override fun getLowStockProducts(): Flow<List<Product>> =
        productDao.getLowStockProducts().map { list -> list.map { it.toDomain() } }

    override fun getProductsByCategory(category: String): Flow<List<Product>> =
        productDao.getProductsByCategory(category).map { list -> list.map { it.toDomain() } }

    override fun getProductsBySupplierId(supplierId: Long): Flow<List<Product>> =
        productDao.getProductsBySupplierId(supplierId).map { list -> list.map { it.toDomain() } }

    override fun getAllCategories(): Flow<List<String>> =
        productDao.getAllCategories()

    override fun getProductCount(): Flow<Int> =
        productDao.getProductCount()

    override fun getLowStockCount(): Flow<Int> =
        productDao.getLowStockCount()

    override fun getOutOfStockCount(): Flow<Int> =
        productDao.getOutOfStockCount()

    override suspend fun getProductById(productId: Long): Product? =
        productDao.getProductById(productId)?.toDomain()

    override suspend fun getProductByCode(code: String): Product? =
        productDao.getProductByCode(code)?.toDomain()

    override suspend fun insertProduct(product: Product): Long =
        productDao.insertProduct(product.toEntity())

    override suspend fun updateProduct(product: Product) =
        productDao.updateProduct(product.toEntity())

    override suspend fun deleteProduct(product: Product) =
        productDao.deleteProduct(product.toEntity())

    override suspend fun updateStock(productId: Long, newStock: Int) {
        val status = when {
            newStock == 0 -> ProductStatus.OUT_OF_STOCK
            else -> ProductStatus.AVAILABLE
        }
        productDao.updateStock(productId, newStock, status)
    }
}
