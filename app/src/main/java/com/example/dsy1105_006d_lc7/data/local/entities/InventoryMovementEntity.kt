package com.example.dsy1105_006d_lc7.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Enum para los tipos de movimiento de inventario.
 */
enum class MovementType {
    ENTRY,
    SALE,
    SERVICE_CONSUMPTION,
    ADJUSTMENT,
    RETURN
}

/**
 * Entidad Room que representa un movimiento de inventario (entrada, salida, ajuste).
 * Registra quién realizó el movimiento, la cantidad, y los stocks previo/resultante.
 */
@Entity(
    tableName = "inventory_movements",
    indices = [Index(value = ["productId"]), Index(value = ["timestamp"])],
    foreignKeys = [
        ForeignKey(
            entity = ProductEntity::class,
            parentColumns = ["id"],
            childColumns = ["productId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class InventoryMovementEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val movementType: MovementType,
    val productId: Long,
    val quantity: Int,
    val timestamp: Long = System.currentTimeMillis(),
    val reason: String,
    val observation: String? = null,
    val previousStock: Int,
    val resultingStock: Int,
    val userId: String
)
