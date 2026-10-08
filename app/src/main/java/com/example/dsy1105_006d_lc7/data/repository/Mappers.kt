package com.example.dsy1105_006d_lc7.data.repository

import com.example.dsy1105_006d_lc7.data.local.entities.*
import com.example.dsy1105_006d_lc7.domain.model.*

/**
 * Funciones de extensión para mapear entre entidades Room y modelos de dominio.
 */

// ── Product Mappers ──

fun ProductEntity.toDomain(): Product = Product(
    id = id,
    code = code,
    name = name,
    description = description,
    category = category,
    brand = brand,
    unitMeasure = unitMeasure,
    currentStock = currentStock,
    minStock = minStock,
    suggestedReplenishment = suggestedReplenishment,
    referenceCostPrice = referenceCostPrice,
    referenceSalePrice = referenceSalePrice,
    status = status.toDomain(),
    lastEntryDate = lastEntryDate,
    lastExitDate = lastExitDate,
    supplierId = supplierId,
    locationId = locationId
)

fun Product.toEntity(): ProductEntity = ProductEntity(
    id = id,
    code = code,
    name = name,
    description = description,
    category = category,
    brand = brand,
    unitMeasure = unitMeasure,
    currentStock = currentStock,
    minStock = minStock,
    suggestedReplenishment = suggestedReplenishment,
    referenceCostPrice = referenceCostPrice,
    referenceSalePrice = referenceSalePrice,
    status = status.toEntity(),
    lastEntryDate = lastEntryDate,
    lastExitDate = lastExitDate,
    supplierId = supplierId,
    locationId = locationId
)

fun ProductStatus.toDomain(): ProductStatusDomain = when (this) {
    ProductStatus.ACTIVE -> ProductStatusDomain.ACTIVE
    ProductStatus.INACTIVE -> ProductStatusDomain.INACTIVE
    ProductStatus.AVAILABLE -> ProductStatusDomain.AVAILABLE
    ProductStatus.LOW_STOCK -> ProductStatusDomain.LOW_STOCK
    ProductStatus.OUT_OF_STOCK -> ProductStatusDomain.OUT_OF_STOCK
    ProductStatus.DISCONTINUED -> ProductStatusDomain.DISCONTINUED
}

fun ProductStatusDomain.toEntity(): ProductStatus = when (this) {
    ProductStatusDomain.ACTIVE -> ProductStatus.ACTIVE
    ProductStatusDomain.INACTIVE -> ProductStatus.INACTIVE
    ProductStatusDomain.AVAILABLE -> ProductStatus.AVAILABLE
    ProductStatusDomain.LOW_STOCK -> ProductStatus.LOW_STOCK
    ProductStatusDomain.OUT_OF_STOCK -> ProductStatus.OUT_OF_STOCK
    ProductStatusDomain.DISCONTINUED -> ProductStatus.DISCONTINUED
}

// ── Movement Mappers ──

fun InventoryMovementEntity.toDomain(): InventoryMovement = InventoryMovement(
    id = id,
    movementType = movementType.toDomain(),
    productId = productId,
    quantity = quantity,
    timestamp = timestamp,
    reason = reason,
    observation = observation,
    previousStock = previousStock,
    resultingStock = resultingStock,
    userId = userId
)

fun InventoryMovement.toEntity(): InventoryMovementEntity = InventoryMovementEntity(
    id = id,
    movementType = movementType.toEntity(),
    productId = productId,
    quantity = quantity,
    timestamp = timestamp,
    reason = reason,
    observation = observation,
    previousStock = previousStock,
    resultingStock = resultingStock,
    userId = userId
)

fun MovementType.toDomain(): MovementTypeDomain = when (this) {
    MovementType.ENTRY -> MovementTypeDomain.ENTRY
    MovementType.SALE -> MovementTypeDomain.SALE
    MovementType.SERVICE_CONSUMPTION -> MovementTypeDomain.SERVICE_CONSUMPTION
    MovementType.ADJUSTMENT -> MovementTypeDomain.ADJUSTMENT
    MovementType.RETURN -> MovementTypeDomain.RETURN
}

fun MovementTypeDomain.toEntity(): MovementType = when (this) {
    MovementTypeDomain.ENTRY -> MovementType.ENTRY
    MovementTypeDomain.SALE -> MovementType.SALE
    MovementTypeDomain.SERVICE_CONSUMPTION -> MovementType.SERVICE_CONSUMPTION
    MovementTypeDomain.ADJUSTMENT -> MovementType.ADJUSTMENT
    MovementTypeDomain.RETURN -> MovementType.RETURN
}

// ── Location Mappers ──

fun LocationEntity.toDomain(): Location = Location(
    id = id,
    code = code,
    zone = zone,
    shelf = shelf,
    rack = rack,
    position = position,
    status = status.toDomain(),
    lastUpdated = lastUpdated
)

fun Location.toEntity(): LocationEntity = LocationEntity(
    id = id,
    code = code,
    zone = zone,
    shelf = shelf,
    rack = rack,
    position = position,
    status = status.toEntity(),
    lastUpdated = lastUpdated
)

fun LocationStatus.toDomain(): LocationStatusDomain = when (this) {
    LocationStatus.AVAILABLE -> LocationStatusDomain.AVAILABLE
    LocationStatus.OCCUPIED -> LocationStatusDomain.OCCUPIED
    LocationStatus.REORGANIZING -> LocationStatusDomain.REORGANIZING
    LocationStatus.OUT_OF_SERVICE -> LocationStatusDomain.OUT_OF_SERVICE
}

fun LocationStatusDomain.toEntity(): LocationStatus = when (this) {
    LocationStatusDomain.AVAILABLE -> LocationStatus.AVAILABLE
    LocationStatusDomain.OCCUPIED -> LocationStatus.OCCUPIED
    LocationStatusDomain.REORGANIZING -> LocationStatus.REORGANIZING
    LocationStatusDomain.OUT_OF_SERVICE -> LocationStatus.OUT_OF_SERVICE
}

// ── Supplier Mappers ──

fun SupplierEntity.toDomain(): Supplier = Supplier(
    id = id,
    code = code,
    companyName = companyName,
    contactName = contactName,
    email = email,
    phone = phone,
    suppliedCategories = suppliedCategories,
    isActive = isActive
)

fun Supplier.toEntity(): SupplierEntity = SupplierEntity(
    id = id,
    code = code,
    companyName = companyName,
    contactName = contactName,
    email = email,
    phone = phone,
    suppliedCategories = suppliedCategories,
    isActive = isActive
)
