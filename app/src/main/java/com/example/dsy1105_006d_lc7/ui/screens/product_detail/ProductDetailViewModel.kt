package com.example.dsy1105_006d_lc7.ui.screens.product_detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dsy1105_006d_lc7.domain.model.InventoryMovement
import com.example.dsy1105_006d_lc7.domain.model.Location
import com.example.dsy1105_006d_lc7.domain.model.Product
import com.example.dsy1105_006d_lc7.domain.model.Supplier
import com.example.dsy1105_006d_lc7.domain.repository.LocationRepository
import com.example.dsy1105_006d_lc7.domain.repository.MovementRepository
import com.example.dsy1105_006d_lc7.domain.repository.ProductRepository
import com.example.dsy1105_006d_lc7.domain.repository.SupplierRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProductDetailUiState(
    val isLoading: Boolean = true,
    val product: Product? = null,
    val supplier: Supplier? = null,
    val location: Location? = null,
    val movements: List<InventoryMovement> = emptyList(),
    val error: String? = null,
    val isDeleted: Boolean = false
)

@HiltViewModel
class ProductDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val productRepository: ProductRepository,
    private val movementRepository: MovementRepository,
    private val supplierRepository: SupplierRepository,
    private val locationRepository: LocationRepository
) : ViewModel() {

    private val productId: Long = checkNotNull(savedStateHandle["productId"]) {
        "productId parameter is required"
    }

    private val _uiState = MutableStateFlow(ProductDetailUiState())
    val uiState: StateFlow<ProductDetailUiState> = _uiState.asStateFlow()

    init {
        loadProductDetails()
    }

    fun loadProductDetails() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val product = productRepository.getProductById(productId)
                if (product == null) {
                    _uiState.update { it.copy(isLoading = false, error = "Producto no encontrado") }
                    return@launch
                }

                val supplier = product.supplierId?.let { supplierRepository.getSupplierById(it) }
                val location = product.locationId?.let { locationRepository.getLocationById(it) }

                // Cargar movimientos del producto
                movementRepository.getMovementsByProduct(productId).collect { movementsList ->
                    _uiState.update { current ->
                        current.copy(
                            isLoading = false,
                            product = product,
                            supplier = supplier,
                            location = location,
                            movements = movementsList
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.localizedMessage) }
            }
        }
    }

    fun deleteProduct(onDeleted: () -> Unit) {
        viewModelScope.launch {
            val product = _uiState.value.product ?: return@launch
            try {
                productRepository.deleteProduct(product)
                _uiState.update { it.copy(isDeleted = true) }
                onDeleted()
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Error al eliminar producto: ${e.localizedMessage}") }
            }
        }
    }
}
