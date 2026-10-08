package com.example.dsy1105_006d_lc7.data.repository

import com.example.dsy1105_006d_lc7.data.local.dao.MovementDao
import com.example.dsy1105_006d_lc7.domain.model.InventoryMovement
import com.example.dsy1105_006d_lc7.domain.repository.MovementRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementación concreta del repositorio de movimientos de inventario.
 */
@Singleton
class MovementRepositoryImpl @Inject constructor(
    private val movementDao: MovementDao
) : MovementRepository {

    override fun getAllMovements(): Flow<List<InventoryMovement>> =
        movementDao.getAllMovements().map { list -> list.map { it.toDomain() } }

    override fun getMovementsByProduct(productId: Long): Flow<List<InventoryMovement>> =
        movementDao.getMovementsByProduct(productId).map { list -> list.map { it.toDomain() } }

    override fun getMovementsByDateRange(startDate: Long, endDate: Long): Flow<List<InventoryMovement>> =
        movementDao.getMovementsByDateRange(startDate, endDate).map { list -> list.map { it.toDomain() } }

    override fun getMovementCount(): Flow<Int> =
        movementDao.getMovementCount()

    override suspend fun getMovementById(movementId: Long): InventoryMovement? =
        movementDao.getMovementById(movementId)?.toDomain()

    override suspend fun insertMovement(movement: InventoryMovement): Long =
        movementDao.insertMovement(movement.toEntity())
}
