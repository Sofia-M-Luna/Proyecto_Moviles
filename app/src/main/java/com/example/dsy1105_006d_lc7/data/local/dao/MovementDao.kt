package com.example.dsy1105_006d_lc7.data.local.dao

import androidx.room.*
import com.example.dsy1105_006d_lc7.data.local.entities.InventoryMovementEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO para registrar y consultar movimientos de inventario.
 * Soporta consultas por producto, tipo de movimiento y rango de fecha.
 */
@Dao
interface MovementDao {

    @Query("SELECT * FROM inventory_movements ORDER BY timestamp DESC")
    fun getAllMovements(): Flow<List<InventoryMovementEntity>>

    @Query("SELECT * FROM inventory_movements WHERE productId = :productId ORDER BY timestamp DESC")
    fun getMovementsByProduct(productId: Long): Flow<List<InventoryMovementEntity>>

    @Query(
        """
        SELECT * FROM inventory_movements 
        WHERE timestamp BETWEEN :startDate AND :endDate 
        ORDER BY timestamp DESC
        """
    )
    fun getMovementsByDateRange(startDate: Long, endDate: Long): Flow<List<InventoryMovementEntity>>

    @Query("SELECT * FROM inventory_movements WHERE id = :movementId")
    suspend fun getMovementById(movementId: Long): InventoryMovementEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovement(movement: InventoryMovementEntity): Long

    @Query("SELECT COUNT(*) FROM inventory_movements")
    fun getMovementCount(): Flow<Int>

    @Query(
        """
        SELECT * FROM inventory_movements 
        WHERE movementType = :type 
        ORDER BY timestamp DESC
        """
    )
    fun getMovementsByType(type: String): Flow<List<InventoryMovementEntity>>
}
