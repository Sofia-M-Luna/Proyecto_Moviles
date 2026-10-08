package com.example.dsy1105_006d_lc7.domain.model

/**
 * Modelo de dominio para Movimiento de Inventario.
 */
data class InventoryMovement(
    val id: Long = 0L,
    val movementType: MovementTypeDomain,
    val productId: Long,
    val productName: String = "",
    val productCode: String = "",
    val quantity: Int,
    val timestamp: Long = System.currentTimeMillis(),
    val reason: String = "",
    val observation: String? = null,
    val previousStock: Int = 0,
    val resultingStock: Int = 0,
    val userId: String = ""
)

enum class MovementTypeDomain {
    ENTRY, SALE, SERVICE_CONSUMPTION, ADJUSTMENT, RETURN;

    val displayName: String
        get() = when (this) {
            ENTRY -> "Entrada"
            SALE -> "Venta"
            SERVICE_CONSUMPTION -> "Consumo de Servicio"
            ADJUSTMENT -> "Ajuste"
            RETURN -> "Devolución"
        }

    /** true si el movimiento incrementa stock */
    val isIncoming: Boolean
        get() = this == ENTRY || this == RETURN || this == ADJUSTMENT
}
