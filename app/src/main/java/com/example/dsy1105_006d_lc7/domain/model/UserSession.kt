package com.example.dsy1105_006d_lc7.domain.model

/**
 * Modelo de dominio para usuario autenticado.
 * Contiene la información de rol para control de acceso.
 */
data class UserSession(
    val username: String,
    val role: UserRole,
    val displayName: String
)

/**
 * Roles disponibles en el sistema.
 * OWNER: Acceso completo.
 * OPERATOR: Restringido (no puede eliminar productos, modificar stock mínimo, ni gestionar proveedores).
 */
enum class UserRole {
    OWNER, OPERATOR;

    val displayName: String
        get() = when (this) {
            OWNER -> "Propietario"
            OPERATOR -> "Operador"
        }

    val canDeleteProducts: Boolean get() = this == OWNER
    val canModifyMinStock: Boolean get() = this == OWNER
    val canManageSuppliers: Boolean get() = this == OWNER
    val canPrepareOrders: Boolean get() = this == OWNER
}
