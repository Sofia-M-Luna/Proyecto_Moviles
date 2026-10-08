package com.example.dsy1105_006d_lc7.domain.model

/**
 * Modelo para representar una solicitud de reposición agrupada por proveedor.
 */
data class ReplenishmentReport(
    val supplier: Supplier,
    val items: List<ReplenishmentItem>,
    val generatedAt: Long = System.currentTimeMillis()
) {
    val totalItemsNeeded: Int get() = items.sumOf { it.quantityNeeded }
    val estimatedTotalCost: Double get() = items.sumOf { it.estimatedCost }
}

data class ReplenishmentItem(
    val product: Product,
    val quantityNeeded: Int,
    val estimatedCost: Double
)
