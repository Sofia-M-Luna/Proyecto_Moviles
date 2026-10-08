package com.example.dsy1105_006d_lc7.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.dsy1105_006d_lc7.data.local.AppDatabase
import com.example.dsy1105_006d_lc7.data.local.dao.LocationDao
import com.example.dsy1105_006d_lc7.data.local.dao.MovementDao
import com.example.dsy1105_006d_lc7.data.local.dao.ProductDao
import com.example.dsy1105_006d_lc7.data.local.dao.SupplierDao
import com.example.dsy1105_006d_lc7.data.local.entities.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Singleton

/**
 * Módulo Hilt que provee la base de datos Room y sus DAOs.
 * Incluye prepoblación con datos ficticios para demostración del MVP.
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Volatile
    private var INSTANCE: AppDatabase? = null

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return INSTANCE ?: synchronized(this) {
            val instance = Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "taller_automotriz_db"
            )
                .addCallback(object : RoomDatabase.Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        CoroutineScope(Dispatchers.IO).launch {
                            INSTANCE?.let { prepopulateDatabase(it) }
                        }
                    }
                })
                .fallbackToDestructiveMigration()
                .build()
            INSTANCE = instance
            instance
        }
    }

    @Provides
    fun provideProductDao(database: AppDatabase): ProductDao = database.productDao()

    @Provides
    fun provideMovementDao(database: AppDatabase): MovementDao = database.movementDao()

    @Provides
    fun provideLocationDao(database: AppDatabase): LocationDao = database.locationDao()

    @Provides
    fun provideSupplierDao(database: AppDatabase): SupplierDao = database.supplierDao()

    /**
     * Prepobla la base de datos con datos ficticios de demostración.
     */
    private suspend fun prepopulateDatabase(db: AppDatabase) {
        // ── Proveedores ficticios ──
        val suppliers = listOf(
            SupplierEntity(
                id = 1, code = "PROV-001", companyName = "AutoParts Chile SpA",
                contactName = "Carlos Méndez", email = "ventas@autopartschile.cl",
                phone = "+56 9 1234 5678",
                suppliedCategories = listOf("Filtros", "Aceites", "Frenos"),
                isActive = true
            ),
            SupplierEntity(
                id = 2, code = "PROV-002", companyName = "Distribuidora MotorPro Ltda",
                contactName = "María González", email = "pedidos@motorpro.cl",
                phone = "+56 9 8765 4321",
                suppliedCategories = listOf("Bujías", "Correas", "Suspensión"),
                isActive = true
            ),
            SupplierEntity(
                id = 3, code = "PROV-003", companyName = "Lubricantes del Sur SA",
                contactName = "Roberto Fuentes", email = "contacto@lubrisur.cl",
                phone = "+56 2 2345 6789",
                suppliedCategories = listOf("Aceites", "Lubricantes", "Refrigerantes"),
                isActive = true
            )
        )
        suppliers.forEach { db.supplierDao().insertSupplier(it) }

        // ── Ubicaciones ficticias ──
        val locations = listOf(
            LocationEntity(id = 1, code = "BOD-A-E1-R1-P1", zone = "Bodega A", shelf = "Estante 1", rack = "Repisa 1", position = "Izquierda", status = LocationStatus.OCCUPIED),
            LocationEntity(id = 2, code = "BOD-A-E1-R2-P1", zone = "Bodega A", shelf = "Estante 1", rack = "Repisa 2", position = "Centro", status = LocationStatus.OCCUPIED),
            LocationEntity(id = 3, code = "BOD-A-E2-R1-P1", zone = "Bodega A", shelf = "Estante 2", rack = "Repisa 1", position = "Derecha", status = LocationStatus.AVAILABLE),
            LocationEntity(id = 4, code = "BOD-B-E1-R1-P1", zone = "Bodega B", shelf = "Estante 1", rack = "Repisa 1", position = "Izquierda", status = LocationStatus.OCCUPIED),
            LocationEntity(id = 5, code = "BOD-B-E1-R2-P1", zone = "Bodega B", shelf = "Estante 1", rack = "Repisa 2", position = "Centro", status = LocationStatus.AVAILABLE),
            LocationEntity(id = 6, code = "TAL-E1-R1-P1", zone = "Taller", shelf = "Estante 1", rack = "Repisa 1", position = "Frontal", status = LocationStatus.OCCUPIED)
        )
        locations.forEach { db.locationDao().insertLocation(it) }

        // ── Productos ficticios ──
        val products = listOf(
            ProductEntity(
                id = 1, code = "FIL-ACE-001", name = "Filtro de Aceite Universal",
                description = "Filtro de aceite para motores gasolina 1.4L-2.0L",
                category = "Filtros", brand = "Mann Filter", unitMeasure = "Unidad",
                currentStock = 15, minStock = 5, suggestedReplenishment = 20,
                referenceCostPrice = 3500.0, referenceSalePrice = 6900.0,
                status = ProductStatus.AVAILABLE, supplierId = 1, locationId = 1
            ),
            ProductEntity(
                id = 2, code = "ACE-5W30-001", name = "Aceite Motor 5W-30 Sintético",
                description = "Aceite sintético 5W-30 para motores modernos, 1 litro",
                category = "Aceites", brand = "Mobil 1", unitMeasure = "Litro",
                currentStock = 3, minStock = 10, suggestedReplenishment = 30,
                referenceCostPrice = 8900.0, referenceSalePrice = 14900.0,
                status = ProductStatus.LOW_STOCK, supplierId = 3, locationId = 2
            ),
            ProductEntity(
                id = 3, code = "PAS-FRE-001", name = "Pastillas de Freno Delanteras",
                description = "Juego de pastillas de freno delanteras cerámicas",
                category = "Frenos", brand = "Bosch", unitMeasure = "Juego",
                currentStock = 8, minStock = 4, suggestedReplenishment = 12,
                referenceCostPrice = 15000.0, referenceSalePrice = 28900.0,
                status = ProductStatus.AVAILABLE, supplierId = 1, locationId = 4
            ),
            ProductEntity(
                id = 4, code = "BUJ-NGK-001", name = "Bujía de Encendido NGK",
                description = "Bujía de iridio para motores gasolina",
                category = "Bujías", brand = "NGK", unitMeasure = "Unidad",
                currentStock = 0, minStock = 8, suggestedReplenishment = 24,
                referenceCostPrice = 4500.0, referenceSalePrice = 8900.0,
                status = ProductStatus.OUT_OF_STOCK, supplierId = 2, locationId = null
            ),
            ProductEntity(
                id = 5, code = "COR-DIS-001", name = "Correa de Distribución",
                description = "Correa de distribución para motores 1.6L-2.0L",
                category = "Correas", brand = "Gates", unitMeasure = "Unidad",
                currentStock = 2, minStock = 3, suggestedReplenishment = 8,
                referenceCostPrice = 25000.0, referenceSalePrice = 45000.0,
                status = ProductStatus.LOW_STOCK, supplierId = 2, locationId = 6
            ),
            ProductEntity(
                id = 6, code = "FIL-AIR-001", name = "Filtro de Aire Motor",
                description = "Filtro de aire de alto flujo para motores gasolina",
                category = "Filtros", brand = "K&N", unitMeasure = "Unidad",
                currentStock = 12, minStock = 5, suggestedReplenishment = 15,
                referenceCostPrice = 12000.0, referenceSalePrice = 22000.0,
                status = ProductStatus.AVAILABLE, supplierId = 1, locationId = 1
            ),
            ProductEntity(
                id = 7, code = "LIQ-REF-001", name = "Líquido Refrigerante Verde",
                description = "Refrigerante anticongelante de larga duración, 1 litro",
                category = "Refrigerantes", brand = "Prestone", unitMeasure = "Litro",
                currentStock = 6, minStock = 8, suggestedReplenishment = 20,
                referenceCostPrice = 5500.0, referenceSalePrice = 9900.0,
                status = ProductStatus.LOW_STOCK, supplierId = 3, locationId = 2
            ),
            ProductEntity(
                id = 8, code = "AMO-DEL-001", name = "Amortiguador Delantero",
                description = "Amortiguador de gas delantero, pieza individual",
                category = "Suspensión", brand = "Monroe", unitMeasure = "Unidad",
                currentStock = 4, minStock = 2, suggestedReplenishment = 6,
                referenceCostPrice = 35000.0, referenceSalePrice = 59900.0,
                status = ProductStatus.AVAILABLE, supplierId = 2, locationId = 4
            )
        )
        products.forEach { db.productDao().insertProduct(it) }
    }
}
