package com.example.dsy1105_006d_lc7.data.repository

import com.example.dsy1105_006d_lc7.data.local.dao.SupplierDao
import com.example.dsy1105_006d_lc7.domain.model.Supplier
import com.example.dsy1105_006d_lc7.domain.repository.SupplierRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementación concreta del repositorio de proveedores.
 */
@Singleton
class SupplierRepositoryImpl @Inject constructor(
    private val supplierDao: SupplierDao
) : SupplierRepository {

    override fun getActiveSuppliers(): Flow<List<Supplier>> =
        supplierDao.getActiveSuppliers().map { list -> list.map { it.toDomain() } }

    override fun getAllSuppliers(): Flow<List<Supplier>> =
        supplierDao.getAllSuppliers().map { list -> list.map { it.toDomain() } }

    override suspend fun getSupplierById(supplierId: Long): Supplier? =
        supplierDao.getSupplierById(supplierId)?.toDomain()

    override suspend fun insertSupplier(supplier: Supplier): Long =
        supplierDao.insertSupplier(supplier.toEntity())

    override suspend fun updateSupplier(supplier: Supplier) =
        supplierDao.updateSupplier(supplier.toEntity())

    override suspend fun deleteSupplier(supplier: Supplier) =
        supplierDao.deleteSupplier(supplier.toEntity())
}
