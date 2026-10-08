package com.example.dsy1105_006d_lc7.domain.repository

import com.example.dsy1105_006d_lc7.domain.model.InventoryMovement
import kotlinx.coroutines.flow.Flow

/**
 * Interfaz de repositorio para movimientos de inventario (Domain Layer).
 */
interface MovementRepository {
    fun getAllMovements(): Flow<List<InventoryMovement>>
    fun getMovementsByProduct(productId: Long): Flow<List<InventoryMovement>>
    fun getMovementsByDateRange(startDate: Long, endDate: Long): Flow<List<InventoryMovement>>
    fun getMovementCount(): Flow<Int>
    suspend fun getMovementById(movementId: Long): InventoryMovement?
    suspend fun insertMovement(movement: InventoryMovement): Long
}
