package com.example.dsy1105_006d_lc7.data.repository

import com.example.dsy1105_006d_lc7.data.local.dao.LocationDao
import com.example.dsy1105_006d_lc7.domain.model.Location
import com.example.dsy1105_006d_lc7.domain.repository.LocationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementación concreta del repositorio de ubicaciones.
 */
@Singleton
class LocationRepositoryImpl @Inject constructor(
    private val locationDao: LocationDao
) : LocationRepository {

    override fun getAllLocations(): Flow<List<Location>> =
        locationDao.getAllLocations().map { list -> list.map { it.toDomain() } }

    override fun getAvailableLocations(): Flow<List<Location>> =
        locationDao.getAvailableLocations().map { list -> list.map { it.toDomain() } }

    override suspend fun getLocationById(locationId: Long): Location? =
        locationDao.getLocationById(locationId)?.toDomain()

    override suspend fun insertLocation(location: Location): Long =
        locationDao.insertLocation(location.toEntity())

    override suspend fun updateLocation(location: Location) =
        locationDao.updateLocation(location.toEntity())

    override suspend fun deleteLocation(location: Location) =
        locationDao.deleteLocation(location.toEntity())
}
