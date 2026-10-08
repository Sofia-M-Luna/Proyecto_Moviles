package com.example.dsy1105_006d_lc7.data.local.dao

import androidx.room.*
import com.example.dsy1105_006d_lc7.data.local.entities.LocationEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO para gestionar ubicaciones físicas en la bodega del taller.
 */
@Dao
interface LocationDao {

    @Query("SELECT * FROM locations ORDER BY zone ASC, shelf ASC, rack ASC")
    fun getAllLocations(): Flow<List<LocationEntity>>

    @Query("SELECT * FROM locations WHERE id = :locationId")
    suspend fun getLocationById(locationId: Long): LocationEntity?

    @Query("SELECT * FROM locations WHERE status = 'AVAILABLE' ORDER BY code ASC")
    fun getAvailableLocations(): Flow<List<LocationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLocation(location: LocationEntity): Long

    @Update
    suspend fun updateLocation(location: LocationEntity)

    @Delete
    suspend fun deleteLocation(location: LocationEntity)
}
