package com.example.dsy1105_006d_lc7.ui.screens.product_entry

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dsy1105_006d_lc7.domain.model.Location
import com.example.dsy1105_006d_lc7.domain.model.Product
import com.example.dsy1105_006d_lc7.domain.model.ProductStatusDomain
import com.example.dsy1105_006d_lc7.domain.model.Supplier
import com.example.dsy1105_006d_lc7.domain.repository.LocationRepository
import com.example.dsy1105_006d_lc7.domain.repository.ProductRepository
import com.example.dsy1105_006d_lc7.domain.repository.SupplierRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AddEditProductUiState(
    val isEditing: Boolean = false,
    val productId: Long? = null,
    val code: String = "",
    val name: String = "",
    val description: String = "",
    val category: String = "Filtros",
    val brand: String = "",
    val unitMeasure: String = "Unidad",
    val currentStock: String = "0",
    val minStock: String = "5",
    val suggestedReplenishment: String = "15",
    val referenceCostPrice: String = "0",
    val referenceSalePrice: String = "0",
    val supplierId: Long = 1L,
    val locationId: Long? = 1L,
    val suppliers: List<Supplier> = emptyList(),
    val locations: List<Location> = emptyList(),
    val categories: List<String> = listOf("Filtros", "Aceites", "Frenos", "Bujías", "Correas", "Refrigerantes", "Suspensión"),
    val units: List<String> = listOf("Unidad", "Litro", "Juego", "Caja", "Par"),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isSaved: Boolean = false
)

@HiltViewModel
class AddEditProductViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val productRepository: ProductRepository,
    private val supplierRepository: SupplierRepository,
    private val locationRepository: LocationRepository
) : ViewModel() {

    private val productIdArg: Long? = savedStateHandle.get<Long>("productId")?.takeIf { it > 0 }

    private val _uiState = MutableStateFlow(AddEditProductUiState())
    val uiState: StateFlow<AddEditProductUiState> = _uiState.asStateFlow()

    init {
        loadDropdownData()
        if (productIdArg != null) {
            loadProduct(productIdArg)
        }
    }

    private fun loadDropdownData() {
        viewModelScope.launch {
            val suppliers = supplierRepository.getActiveSuppliers().firstOrNull() ?: emptyList()
            val locations = locationRepository.getAllLocations().firstOrNull() ?: emptyList()
            _uiState.update {
                it.copy(
                    suppliers = suppliers,
                    locations = locations,
                    supplierId = suppliers.firstOrNull()?.id ?: 1L,
                    locationId = locations.firstOrNull()?.id
                )
            }
        }
    }

    private fun loadProduct(productId: Long) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val product = productRepository.getProductById(productId)
            if (product != null) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isEditing = true,
                        productId = product.id,
                        code = product.code,
                        name = product.name,
                        description = product.description,
                        category = product.category,
                        brand = product.brand,
                        unitMeasure = product.unitMeasure,
                        currentStock = product.currentStock.toString(),
                        minStock = product.minStock.toString(),
                        suggestedReplenishment = product.suggestedReplenishment.toString(),
                        referenceCostPrice = product.referenceCostPrice.toInt().toString(),
                        referenceSalePrice = product.referenceSalePrice.toInt().toString(),
                        supplierId = product.supplierId ?: 1L,
                        locationId = product.locationId
                    )
                }
            } else {
                _uiState.update { it.copy(isLoading = false, errorMessage = "Producto no encontrado") }
            }
        }
    }

    fun onCodeChange(value: String) = _uiState.update { it.copy(code = value) }
    fun onNameChange(value: String) = _uiState.update { it.copy(name = value) }
    fun onDescriptionChange(value: String) = _uiState.update { it.copy(description = value) }
    fun onCategoryChange(value: String) = _uiState.update { it.copy(category = value) }
    fun onBrandChange(value: String) = _uiState.update { it.copy(brand = value) }
    fun onUnitMeasureChange(value: String) = _uiState.update { it.copy(unitMeasure = value) }
    fun onCurrentStockChange(value: String) = _uiState.update { it.copy(currentStock = value) }
    fun onMinStockChange(value: String) = _uiState.update { it.copy(minStock = value) }
    fun onSuggestedReplenishmentChange(value: String) = _uiState.update { it.copy(suggestedReplenishment = value) }
    fun onCostPriceChange(value: String) = _uiState.update { it.copy(referenceCostPrice = value) }
    fun onSalePriceChange(value: String) = _uiState.update { it.copy(referenceSalePrice = value) }
    fun onSupplierChange(id: Long) = _uiState.update { it.copy(supplierId = id) }
    fun onLocationChange(id: Long?) = _uiState.update { it.copy(locationId = id) }

    fun saveProduct(onSuccess: () -> Unit) {
        val state = _uiState.value

        if (state.code.isBlank()) {
            _uiState.update { it.copy(errorMessage = "El código es obligatorio") }
            return
        }
        if (state.name.isBlank()) {
            _uiState.update { it.copy(errorMessage = "El nombre del producto es obligatorio") }
            return
        }

        val stock = state.currentStock.toIntOrNull() ?: 0
        val minStock = state.minStock.toIntOrNull() ?: 0
        val suggested = state.suggestedReplenishment.toIntOrNull() ?: (minStock * 2)
        val cost = state.referenceCostPrice.toDoubleOrNull() ?: 0.0
        val sale = state.referenceSalePrice.toDoubleOrNull() ?: 0.0

        val status = when {
            stock == 0 -> ProductStatusDomain.OUT_OF_STOCK
            stock <= minStock -> ProductStatusDomain.LOW_STOCK
            else -> ProductStatusDomain.AVAILABLE
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                if (state.isEditing && state.productId != null) {
                    val existing = productRepository.getProductById(state.productId)
                    val updated = (existing ?: Product(
                        code = state.code,
                        name = state.name,
                        description = state.description,
                        category = state.category,
                        brand = state.brand,
                        unitMeasure = state.unitMeasure,
                        currentStock = stock,
                        minStock = minStock,
                        suggestedReplenishment = suggested,
                        referenceCostPrice = cost,
                        referenceSalePrice = sale,
                        status = status,
                        supplierId = state.supplierId,
                        locationId = state.locationId
                    )).copy(
                        code = state.code,
                        name = state.name,
                        description = state.description,
                        category = state.category,
                        brand = state.brand,
                        unitMeasure = state.unitMeasure,
                        currentStock = stock,
                        minStock = minStock,
                        suggestedReplenishment = suggested,
                        referenceCostPrice = cost,
                        referenceSalePrice = sale,
                        status = status,
                        supplierId = state.supplierId,
                        locationId = state.locationId
                    )
                    productRepository.updateProduct(updated)
                } else {
                    val newProduct = Product(
                        code = state.code,
                        name = state.name,
                        description = state.description,
                        category = state.category,
                        brand = state.brand,
                        unitMeasure = state.unitMeasure,
                        currentStock = stock,
                        minStock = minStock,
                        suggestedReplenishment = suggested,
                        referenceCostPrice = cost,
                        referenceSalePrice = sale,
                        status = status,
                        supplierId = state.supplierId,
                        locationId = state.locationId
                    )
                    productRepository.insertProduct(newProduct)
                }
                _uiState.update { it.copy(isLoading = false, isSaved = true) }
                onSuccess()
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = "Error al guardar: ${e.localizedMessage}") }
            }
        }
    }
}
