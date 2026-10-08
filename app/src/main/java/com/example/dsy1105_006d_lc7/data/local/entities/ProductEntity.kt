package com.example.dsy1105_006d_lc7.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Enum para el estado del producto en el sistema de inventario.
 */
enum class ProductStatus {
    ACTIVE,
    INACTIVE,
    AVAILABLE,
    LOW_STOCK,
    OUT_OF_STOCK,
    DISCONTINUED
}

/**
 * Entidad Room que representa un producto automotriz en el inventario.
 * Contiene información completa de stock, precios, proveedor y ubicación.
 */
@Entity(
    tableName = "products",
    indices = [
        Index(value = ["code"], unique = true),
        Index(value = ["supplierId"]),
        Index(value = ["locationId"])
    ],
    foreignKeys = [
        ForeignKey(
            entity = SupplierEntity::class,
            parentColumns = ["id"],
            childColumns = ["supplierId"],
            onDelete = ForeignKey.SET_NULL
        ),
        ForeignKey(
            entity = LocationEntity::class,
            parentColumns = ["id"],
            childColumns = ["locationId"],
            onDelete = ForeignKey.SET_NULL
        )
    ]
)
data class ProductEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val code: String,
    val name: String,
    val description: String,
    val category: String,
    val brand: String,
    val unitMeasure: String,
    val currentStock: Int,
    val minStock: Int,
    val suggestedReplenishment: Int,
    val referenceCostPrice: Double,
    val referenceSalePrice: Double,
    val status: ProductStatus = ProductStatus.ACTIVE,
    val lastEntryDate: Long? = null,
    val lastExitDate: Long? = null,
    val supplierId: Long? = null,
    val locationId: Long? = null
)
