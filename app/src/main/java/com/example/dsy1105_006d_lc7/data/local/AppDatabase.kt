package com.example.dsy1105_006d_lc7.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.dsy1105_006d_lc7.data.local.dao.LocationDao
import com.example.dsy1105_006d_lc7.data.local.dao.MovementDao
import com.example.dsy1105_006d_lc7.data.local.dao.ProductDao
import com.example.dsy1105_006d_lc7.data.local.dao.SupplierDao
import com.example.dsy1105_006d_lc7.data.local.entities.InventoryMovementEntity
import com.example.dsy1105_006d_lc7.data.local.entities.LocationEntity
import com.example.dsy1105_006d_lc7.data.local.entities.ProductEntity
import com.example.dsy1105_006d_lc7.data.local.entities.StringListConverter
import com.example.dsy1105_006d_lc7.data.local.entities.SupplierEntity

/**
 * Base de datos principal de la aplicación de inventario automotriz.
 * Contiene las tablas: products, inventory_movements, locations, suppliers.
 */
@Database(
    entities = [
        ProductEntity::class,
        InventoryMovementEntity::class,
        LocationEntity::class,
        SupplierEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(StringListConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao
    abstract fun movementDao(): MovementDao
    abstract fun locationDao(): LocationDao
    abstract fun supplierDao(): SupplierDao
}
