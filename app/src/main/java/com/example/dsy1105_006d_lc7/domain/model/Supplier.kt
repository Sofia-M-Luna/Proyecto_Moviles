package com.example.dsy1105_006d_lc7.domain.model

/**
 * Modelo de dominio para Proveedor.
 */
data class Supplier(
    val id: Long = 0L,
    val code: String = "",
    val companyName: String = "",
    val contactName: String = "",
    val email: String = "",
    val phone: String? = null,
    val suppliedCategories: List<String> = emptyList(),
    val isActive: Boolean = true
)
