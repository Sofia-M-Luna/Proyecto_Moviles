package com.example.dsy1105_006d_lc7.domain.usecase

import com.example.dsy1105_006d_lc7.domain.model.Product
import com.example.dsy1105_006d_lc7.domain.repository.ProductRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Caso de uso para obtener productos con stock bajo (currentStock <= minStock).
 * Utilizado en la pantalla de reposición y alertas.
 */
class GetLowStockProductsUseCase @Inject constructor(
    private val productRepository: ProductRepository
) {
    operator fun invoke(): Flow<List<Product>> {
        return productRepository.getLowStockProducts()
    }
}
