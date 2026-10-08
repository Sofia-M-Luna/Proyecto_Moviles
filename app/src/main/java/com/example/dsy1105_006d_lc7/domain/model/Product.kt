package com.example.dsy1105_006d_lc7.domain.model

/**
 * Modelo de dominio limpio para Producto.
 * Desacoplado de la capa de datos (Room Entity / DTO).
 */
data class Product(
    val id: Long = 0L,
    val code: String = "",
    val name: String = "",
    val description: String = "",
    val category: String = "",
    val brand: String = "",
    val unitMeasure: String = "Unidad",
    val currentStock: Int = 0,
    val minStock: Int = 0,
    val suggestedReplenishment: Int = 0,
    val referenceCostPrice: Double = 0.0,
    val referenceSalePrice: Double = 0.0,
    val status: ProductStatusDomain = ProductStatusDomain.ACTIVE,
    val lastEntryDate: Long? = null,
    val lastExitDate: Long? = null,
    val supplierId: Long? = null,
    val locationId: Long? = null,
    val supplierName: String? = null
) {
    /** Calcula si el producto tiene stock bajo */
    val isLowStock: Boolean get() = currentStock <= minStock && currentStock > 0

    /** Calcula si el producto está sin stock */
    val isOutOfStock: Boolean get() = currentStock == 0

    /** Cantidad sugerida de reposición: TargetStock - CurrentStock */
    val replenishmentQuantity: Int
        get() {
            val targetStock = if (suggestedReplenishment > 0) suggestedReplenishment else minStock * 2
            return (targetStock - currentStock).coerceAtLeast(0)
        }

    /** Margen de ganancia estimado */
    val profitMargin: Double
        get() = if (referenceCostPrice > 0) {
            ((referenceSalePrice - referenceCostPrice) / referenceCostPrice) * 100
        } else 0.0
}

enum class ProductStatusDomain {
    ACTIVE, INACTIVE, AVAILABLE, LOW_STOCK, OUT_OF_STOCK, DISCONTINUED
}
