package com.example.dsy1105_006d_lc7.data.local.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Enum para el estado de una ubicación en la bodega.
 */
enum class LocationStatus {
    AVAILABLE,
    OCCUPIED,
    REORGANIZING,
    OUT_OF_SERVICE
}

/**
 * Entidad Room que representa una ubicación física en la bodega del taller.
 * Usa un código jerárquico (Zona-Estante-Repisa-Posición).
 */
@Entity(
    tableName = "locations",
    indices = [Index(value = ["code"], unique = true)]
)
data class LocationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val code: String,
    val zone: String,
    val shelf: String,
    val rack: String,
    val position: String,
    val status: LocationStatus = LocationStatus.AVAILABLE,
    val lastUpdated: Long = System.currentTimeMillis()
)
