package com.example.dsy1105_006d_lc7.domain.usecase

import com.example.dsy1105_006d_lc7.domain.model.Product
import com.example.dsy1105_006d_lc7.domain.repository.ProductRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Caso de uso para buscar productos por nombre, código, categoría o marca.
 */
class SearchProductsUseCase @Inject constructor(
    private val productRepository: ProductRepository
) {
    operator fun invoke(query: String): Flow<List<Product>> {
        return if (query.isBlank()) {
            productRepository.getAllProducts()
        } else {
            productRepository.searchProducts(query.trim())
        }
    }
}
