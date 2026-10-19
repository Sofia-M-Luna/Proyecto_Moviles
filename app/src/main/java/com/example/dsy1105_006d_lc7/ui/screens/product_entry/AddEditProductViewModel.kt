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
            supplierRepository.getActiveSuppliers().collect { suppliers ->
                _uiState.update { current ->
                    current.copy(
                        suppliers = suppliers,
                        supplierId = if (current.supplierId in suppliers.map { it.id }) current.supplierId else (suppliers.firstOrNull()?.id ?: 1L)
                    )
                }
            }
        }
        viewModelScope.launch {
            locationRepository.getAllLocations().collect { locations ->
                _uiState.update { current ->
                    current.copy(
                        locations = locations,
                        locationId = current.locationId ?: locations.firstOrNull()?.id
                    )
                }
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

    /**
     * Procesa un código QR o de barras escaneado (o ingresado).
     * Si contiene el ID de un producto o su código de parte, busca el producto en la base de datos
     * y rellena automáticamente todos los campos del formulario.
     */
    fun onQrCodeScanned(qrContent: String) {
        val cleanContent = qrContent.trim()
        if (cleanContent.isBlank()) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            // 1. Extraer ID numérico directo (ej: "1", "2")
            val directId = cleanContent.toLongOrNull()

            // 2. Extraer ID con patrones comunes: "ID: 1", "id=1", "id: 1", "producto: 1", "{"id": 1}", "?id=1"
            val extractedId = directId
                ?: Regex("""(?i)\b(?:id|producto|product)[\s:=]+(\d+)\b""").find(cleanContent)?.groupValues?.get(1)?.toLongOrNull()
                ?: Regex("""\"id\"\s*:\s*(\d+)""").find(cleanContent)?.groupValues?.get(1)?.toLongOrNull()
                ?: Regex("""[?&]id=(\d+)""").find(cleanContent)?.groupValues?.get(1)?.toLongOrNull()

            // Buscar en repositorio por ID primero
            var foundProduct: Product? = extractedId?.let { productRepository.getProductById(it) }

            // Si no se encuentra por ID, buscar por código exacto de repuesto (ej: "FIL-ACE-001")
            if (foundProduct == null) {
                foundProduct = productRepository.getProductByCode(cleanContent)
            }

            // Fallback: buscar en el catálogo completo
            if (foundProduct == null) {
                val allProducts = productRepository.getAllProducts().firstOrNull() ?: emptyList()
                foundProduct = (extractedId?.let { id -> allProducts.find { it.id == id } })
                    ?: allProducts.find { it.code.equals(cleanContent, ignoreCase = true) }
            }

            if (foundProduct != null) {
                // Rellenar automáticamente todos los campos del formulario con los datos del repuesto
                _uiState.update { current ->
                    current.copy(
                        isLoading = false,
                        isEditing = true,
                        productId = foundProduct.id,
                        code = foundProduct.code,
                        name = foundProduct.name,
                        description = foundProduct.description,
                        category = foundProduct.category,
                        brand = foundProduct.brand,
                        unitMeasure = foundProduct.unitMeasure,
                        currentStock = foundProduct.currentStock.toString(),
                        minStock = foundProduct.minStock.toString(),
                        suggestedReplenishment = foundProduct.suggestedReplenishment.toString(),
                        referenceCostPrice = foundProduct.referenceCostPrice.toInt().toString(),
                        referenceSalePrice = foundProduct.referenceSalePrice.toInt().toString(),
                        supplierId = foundProduct.supplierId ?: (current.suppliers.firstOrNull()?.id ?: 1L),
                        locationId = foundProduct.locationId ?: current.locationId,
                        errorMessage = null
                    )
                }
            } else {
                // Si no existe, dejamos el código en el campo para registrar un nuevo producto
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        code = cleanContent,
                        errorMessage = "No se encontró ningún repuesto registrado con ID o código '$cleanContent'. Se asignó al código para nuevo registro."
                    )
                }
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
