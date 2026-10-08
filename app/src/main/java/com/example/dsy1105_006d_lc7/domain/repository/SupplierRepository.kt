package com.example.dsy1105_006d_lc7.domain.repository

import com.example.dsy1105_006d_lc7.domain.model.Supplier
import kotlinx.coroutines.flow.Flow

/**
 * Interfaz de repositorio para proveedores (Domain Layer).
 */
interface SupplierRepository {
    fun getActiveSuppliers(): Flow<List<Supplier>>
    fun getAllSuppliers(): Flow<List<Supplier>>
    suspend fun getSupplierById(supplierId: Long): Supplier?
    suspend fun insertSupplier(supplier: Supplier): Long
    suspend fun updateSupplier(supplier: Supplier)
    suspend fun deleteSupplier(supplier: Supplier)
}
