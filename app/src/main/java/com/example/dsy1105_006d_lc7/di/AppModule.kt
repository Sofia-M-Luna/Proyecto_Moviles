package com.example.dsy1105_006d_lc7.di

import com.example.dsy1105_006d_lc7.data.repository.LocationRepositoryImpl
import com.example.dsy1105_006d_lc7.data.repository.MovementRepositoryImpl
import com.example.dsy1105_006d_lc7.data.repository.ProductRepositoryImpl
import com.example.dsy1105_006d_lc7.data.repository.SupplierRepositoryImpl
import com.example.dsy1105_006d_lc7.domain.repository.LocationRepository
import com.example.dsy1105_006d_lc7.domain.repository.MovementRepository
import com.example.dsy1105_006d_lc7.domain.repository.ProductRepository
import com.example.dsy1105_006d_lc7.domain.repository.SupplierRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Módulo Hilt principal que enlaza las interfaces de repositorio
 * con sus implementaciones concretas.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {

    @Binds
    @Singleton
    abstract fun bindProductRepository(impl: ProductRepositoryImpl): ProductRepository

    @Binds
    @Singleton
    abstract fun bindMovementRepository(impl: MovementRepositoryImpl): MovementRepository

    @Binds
    @Singleton
    abstract fun bindSupplierRepository(impl: SupplierRepositoryImpl): SupplierRepository

    @Binds
    @Singleton
    abstract fun bindLocationRepository(impl: LocationRepositoryImpl): LocationRepository
}
