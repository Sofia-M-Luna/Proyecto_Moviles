package com.example.dsy1105_006d_lc7.data.local.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters

/**
 * Conversor de tipos para listas de String ↔ JSON almacenado en Room.
 */
class StringListConverter {
    @TypeConverter
    fun fromStringList(value: List<String>): String {
        return value.joinToString(separator = "|||")
    }

    @TypeConverter
    fun toStringList(value: String): List<String> {
        return if (value.isEmpty()) emptyList() else value.split("|||")
    }
}

/**
 * Entidad Room que representa un proveedor de productos automotrices.
 * Almacena datos de contacto y las categorías que suministra.
 */
@Entity(
    tableName = "suppliers",
    indices = [Index(value = ["code"], unique = true)]
)
@TypeConverters(StringListConverter::class)
data class SupplierEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val code: String,
    val companyName: String,
    val contactName: String,
    val email: String,
    val phone: String? = null,
    val suppliedCategories: List<String> = emptyList(),
    val isActive: Boolean = true
)
