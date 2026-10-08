package com.example.dsy1105_006d_lc7.data.local.dao

import androidx.room.*
import com.example.dsy1105_006d_lc7.data.local.entities.SupplierEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO para gestionar proveedores de productos automotrices.
 */
@Dao
interface SupplierDao {

    @Query("SELECT * FROM suppliers WHERE isActive = 1 ORDER BY companyName ASC")
    fun getActiveSuppliers(): Flow<List<SupplierEntity>>

    @Query("SELECT * FROM suppliers ORDER BY companyName ASC")
    fun getAllSuppliers(): Flow<List<SupplierEntity>>

    @Query("SELECT * FROM suppliers WHERE id = :supplierId")
    suspend fun getSupplierById(supplierId: Long): SupplierEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSupplier(supplier: SupplierEntity): Long

    @Update
    suspend fun updateSupplier(supplier: SupplierEntity)

    @Delete
    suspend fun deleteSupplier(supplier: SupplierEntity)
}
