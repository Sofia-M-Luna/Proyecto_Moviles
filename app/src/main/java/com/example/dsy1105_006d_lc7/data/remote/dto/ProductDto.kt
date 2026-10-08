package com.example.dsy1105_006d_lc7.data.remote.dto

import com.squareup.moshi.JsonClass

/**
 * DTO para transferencia de datos de producto vía API REST.
 */
@JsonClass(generateAdapter = true)
data class ProductDto(
    val id: Long? = null,
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
    val status: String,
    val lastEntryDate: Long? = null,
    val lastExitDate: Long? = null,
    val supplierId: Long? = null,
    val locationId: Long? = null
)
