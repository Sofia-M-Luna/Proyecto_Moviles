package com.example.dsy1105_006d_lc7.domain.model

/**
 * Modelo de dominio para Ubicación en bodega.
 */
data class Location(
    val id: Long = 0L,
    val code: String = "",
    val zone: String = "",
    val shelf: String = "",
    val rack: String = "",
    val position: String = "",
    val status: LocationStatusDomain = LocationStatusDomain.AVAILABLE,
    val lastUpdated: Long = System.currentTimeMillis()
) {
    val fullDescription: String
        get() = "$zone → $shelf → $rack → $position"
}

enum class LocationStatusDomain {
    AVAILABLE, OCCUPIED, REORGANIZING, OUT_OF_SERVICE;

    val displayName: String
        get() = when (this) {
            AVAILABLE -> "Disponible"
            OCCUPIED -> "Ocupada"
            REORGANIZING -> "En Reorganización"
            OUT_OF_SERVICE -> "Fuera de Servicio"
        }
}
