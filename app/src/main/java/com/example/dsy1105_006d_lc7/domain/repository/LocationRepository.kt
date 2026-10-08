package com.example.dsy1105_006d_lc7.domain.repository

import com.example.dsy1105_006d_lc7.domain.model.Location
import kotlinx.coroutines.flow.Flow

/**
 * Interfaz de repositorio para ubicaciones de bodega (Domain Layer).
 */
interface LocationRepository {
    fun getAllLocations(): Flow<List<Location>>
    fun getAvailableLocations(): Flow<List<Location>>
    suspend fun getLocationById(locationId: Long): Location?
    suspend fun insertLocation(location: Location): Long
    suspend fun updateLocation(location: Location)
    suspend fun deleteLocation(location: Location)
}
