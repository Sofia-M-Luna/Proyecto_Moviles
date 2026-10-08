package com.example.dsy1105_006d_lc7.ui.screens.product_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dsy1105_006d_lc7.domain.model.Product
import com.example.dsy1105_006d_lc7.domain.usecase.SearchProductsUseCase
import com.example.dsy1105_006d_lc7.domain.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Estado de la UI para la lista de productos.
 */
data class ProductListUiState(
    val products: List<Product> = emptyList(),
    val searchQuery: String = "",
    val isLoading: Boolean = true,
    val totalProducts: Int = 0,
    val lowStockCount: Int = 0,
    val outOfStockCount: Int = 0,
    val error: String? = null
)

/**
 * ViewModel para la pantalla de listado de productos.
 * Soporta búsqueda reactiva y contadores de stock en tiempo real.
 */
@HiltViewModel
class ProductListViewModel @Inject constructor(
    private val searchProductsUseCase: SearchProductsUseCase,
    private val productRepository: ProductRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")

    private val _uiState = MutableStateFlow(ProductListUiState())
    val uiState: StateFlow<ProductListUiState> = _uiState.asStateFlow()

    init {
        observeProducts()
        observeCounters()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun observeProducts() {
        viewModelScope.launch {
            _searchQuery
                .debounce(300)
                .flatMapLatest { query -> searchProductsUseCase(query) }
                .catch { e ->
                    _uiState.update { it.copy(error = e.message, isLoading = false) }
                }
                .collect { products ->
                    _uiState.update {
                        it.copy(
                            products = products,
                            isLoading = false,
                            error = null
                        )
                    }
                }
        }
    }

    private fun observeCounters() {
        viewModelScope.launch {
            productRepository.getProductCount().collect { count ->
                _uiState.update { it.copy(totalProducts = count) }
            }
        }
        viewModelScope.launch {
            productRepository.getLowStockCount().collect { count ->
                _uiState.update { it.copy(lowStockCount = count) }
            }
        }
        viewModelScope.launch {
            productRepository.getOutOfStockCount().collect { count ->
                _uiState.update { it.copy(outOfStockCount = count) }
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun deleteProduct(product: Product) {
        viewModelScope.launch {
            try {
                productRepository.deleteProduct(product)
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Error al eliminar: ${e.message}") }
            }
        }
    }
}
