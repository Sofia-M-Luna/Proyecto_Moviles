package com.example.dsy1105_006d_lc7.domain.usecase

import com.example.dsy1105_006d_lc7.domain.model.InventoryMovement
import com.example.dsy1105_006d_lc7.domain.model.MovementTypeDomain
import com.example.dsy1105_006d_lc7.domain.model.ProductStatusDomain
import com.example.dsy1105_006d_lc7.domain.repository.MovementRepository
import com.example.dsy1105_006d_lc7.domain.repository.ProductRepository
import javax.inject.Inject

/**
 * Caso de uso para registrar un movimiento de inventario.
 *
 * REGLAS DE NEGOCIO:
 * 1. Impide movimientos de salida que generen stock negativo.
 * 2. Actualiza atómicamente el stock del producto y crea el registro del movimiento.
 * 3. Actualiza las fechas de última entrada/salida según corresponda.
 * 4. Recalcula el estado del producto basado en el nuevo stock.
 */
class RegisterMovementUseCase @Inject constructor(
    private val productRepository: ProductRepository,
    private val movementRepository: MovementRepository
) {
    /**
     * Registra un movimiento de inventario.
     * @return Result.success con el ID del movimiento, o Result.failure con el error.
     */
    suspend operator fun invoke(
        productId: Long,
        movementType: MovementTypeDomain,
        quantity: Int,
        reason: String,
        observation: String?,
        userId: String
    ): Result<Long> {
        // Validaciones básicas
        if (quantity <= 0) {
            return Result.failure(IllegalArgumentException("La cantidad debe ser mayor a 0"))
        }

        val product = productRepository.getProductById(productId)
            ?: return Result.failure(IllegalArgumentException("Producto no encontrado"))

        val previousStock = product.currentStock

        // Calcular nuevo stock según tipo de movimiento
        val newStock = when (movementType) {
            MovementTypeDomain.ENTRY, MovementTypeDomain.RETURN -> previousStock + quantity
            MovementTypeDomain.SALE, MovementTypeDomain.SERVICE_CONSUMPTION -> previousStock - quantity
            MovementTypeDomain.ADJUSTMENT -> quantity // En ajuste, la cantidad es el nuevo stock directo
        }

        // REGLA: Impedir stock negativo en movimientos de salida
        if (newStock < 0) {
            return Result.failure(
                IllegalStateException(
                    "Stock insuficiente. Disponible: $previousStock, solicitado: $quantity"
                )
            )
        }

        // Crear el movimiento
        val movement = InventoryMovement(
            movementType = movementType,
            productId = productId,
            quantity = quantity,
            reason = reason,
            observation = observation,
            previousStock = previousStock,
            resultingStock = newStock,
            userId = userId,
            timestamp = System.currentTimeMillis()
        )

        return try {
            // Actualizar stock del producto
            productRepository.updateStock(productId, newStock)

            // Registrar movimiento
            val movementId = movementRepository.insertMovement(movement)

            // Actualizar fechas según tipo
            val now = System.currentTimeMillis()
            val updatedProduct = product.copy(
                currentStock = newStock,
                status = calculateStatus(newStock, product.minStock),
                lastEntryDate = if (movementType.isIncoming) now else product.lastEntryDate,
                lastExitDate = if (!movementType.isIncoming) now else product.lastExitDate
            )
            productRepository.updateProduct(updatedProduct)

            Result.success(movementId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun calculateStatus(currentStock: Int, minStock: Int): ProductStatusDomain {
        return when {
            currentStock == 0 -> ProductStatusDomain.OUT_OF_STOCK
            currentStock <= minStock -> ProductStatusDomain.LOW_STOCK
            else -> ProductStatusDomain.AVAILABLE
        }
    }
}
