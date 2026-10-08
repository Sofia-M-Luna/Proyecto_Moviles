package com.example.dsy1105_006d_lc7.data.local.dao

import androidx.room.*
import com.example.dsy1105_006d_lc7.data.local.entities.ProductEntity
import com.example.dsy1105_006d_lc7.data.local.entities.ProductStatus
import kotlinx.coroutines.flow.Flow

/**
 * DAO para operaciones CRUD sobre productos.
 * Incluye queries para búsqueda, filtrado por stock bajo, y actualización atómica de stock.
 */
@Dao
interface ProductDao {

    @Query("SELECT * FROM products ORDER BY name ASC")
    fun getAllProducts(): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE id = :productId")
    suspend fun getProductById(productId: Long): ProductEntity?

    @Query("SELECT * FROM products WHERE code = :code")
    suspend fun getProductByCode(code: String): ProductEntity?

    @Query(
        """
        SELECT * FROM products 
        WHERE name LIKE '%' || :query || '%' 
        OR code LIKE '%' || :query || '%' 
        OR category LIKE '%' || :query || '%'
        OR brand LIKE '%' || :query || '%'
        ORDER BY name ASC
        """
    )
    fun searchProducts(query: String): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE currentStock <= minStock AND status != 'DISCONTINUED'")
    fun getLowStockProducts(): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE category = :category ORDER BY name ASC")
    fun getProductsByCategory(category: String): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE supplierId = :supplierId ORDER BY name ASC")
    fun getProductsBySupplierId(supplierId: Long): Flow<List<ProductEntity>>

    @Query("SELECT DISTINCT category FROM products ORDER BY category ASC")
    fun getAllCategories(): Flow<List<String>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: ProductEntity): Long

    @Update
    suspend fun updateProduct(product: ProductEntity)

    @Delete
    suspend fun deleteProduct(product: ProductEntity)

    @Query("UPDATE products SET currentStock = :newStock, status = :newStatus WHERE id = :productId")
    suspend fun updateStock(productId: Long, newStock: Int, newStatus: ProductStatus)

    @Query("UPDATE products SET lastEntryDate = :timestamp WHERE id = :productId")
    suspend fun updateLastEntryDate(productId: Long, timestamp: Long)

    @Query("UPDATE products SET lastExitDate = :timestamp WHERE id = :productId")
    suspend fun updateLastExitDate(productId: Long, timestamp: Long)

    @Query("SELECT COUNT(*) FROM products")
    fun getProductCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM products WHERE currentStock <= minStock AND status != 'DISCONTINUED'")
    fun getLowStockCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM products WHERE currentStock = 0 AND status != 'DISCONTINUED'")
    fun getOutOfStockCount(): Flow<Int>
}
