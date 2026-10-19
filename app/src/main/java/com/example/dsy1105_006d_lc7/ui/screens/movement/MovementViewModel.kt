package com.example.dsy1105_006d_lc7.ui.screens.movement

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.dsy1105_006d_lc7.domain.model.MovementTypeDomain
import com.example.dsy1105_006d_lc7.domain.model.Product
import com.example.dsy1105_006d_lc7.domain.repository.ProductRepository
import com.example.dsy1105_006d_lc7.domain.usecase.RegisterMovementUseCase
import com.example.dsy1105_006d_lc7.util.StockNotificationHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MovementUiState(
    val selectedProductId: Long? = null,
    val selectedProduct: Product? = null,
    val allProducts: List<Product> = emptyList(),
    val movementType: MovementTypeDomain = MovementTypeDomain.SALE,
    val quantity: Int = 1,
    val reason: String = "Venta a cliente en mostrador",
    val observation: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isSuccess: Boolean = false
) {
    val projectedStock: Int?
        get() {
            val current = selectedProduct?.currentStock ?: return null
            return when (movementType) {
                MovementTypeDomain.ENTRY, MovementTypeDomain.RETURN -> current + quantity
                MovementTypeDomain.SALE, MovementTypeDomain.SERVICE_CONSUMPTION -> current - quantity
                MovementTypeDomain.ADJUSTMENT -> quantity
            }
        }

    val isNegativeStockRisk: Boolean
        get() = (projectedStock ?: 0) < 0
}

@HiltViewModel
class MovementViewModel @Inject constructor(
    application: Application,
    savedStateHandle: SavedStateHandle,
    private val productRepository: ProductRepository,
    private val registerMovementUseCase: RegisterMovementUseCase
) : AndroidViewModel(application) {

    private val preselectedProductId: Long? =
        savedStateHandle.get<Long>("productId")?.takeIf { it > 0 }

    private val _uiState = MutableStateFlow(MovementUiState())
    val uiState: StateFlow<MovementUiState> = _uiState.asStateFlow()

    init {
        loadProducts()
    }

    private fun loadProducts() {
        viewModelScope.launch {
            productRepository.getAllProducts().collect { products ->
                _uiState.update { current ->
                    val selected = if (current.selectedProductId != null) {
                        products.find { it.id == current.selectedProductId } ?: current.selectedProduct
                    } else {
                        preselectedProductId?.let { id -> products.find { it.id == id } }
                            ?: products.firstOrNull()
                    }
                    current.copy(
                        allProducts = products,
                        selectedProductId = selected?.id,
                        selectedProduct = selected
                    )
                }
            }
        }
    }

    fun onProductSelected(product: Product) {
        _uiState.update {
            it.copy(
                selectedProductId = product.id,
                selectedProduct = product,
                errorMessage = null
            )
        }
    }

    /**
     * Procesa el contenido leído desde un código QR.
     * Extrae el ID del producto (o su código de parte), lo busca en el catálogo
     * y muestra automáticamente sus datos en el movimiento.
     */
    fun onQrCodeScanned(qrContent: String) {
        val cleanContent = qrContent.trim()
        if (cleanContent.isBlank()) return

        viewModelScope.launch {
            // 1. Búsqueda por ID numérico directo (ej: "1", "2", "3")
            val directId = cleanContent.toLongOrNull()

            // 2. Búsqueda por patrón explícito de ID (ej: "ID: 1", "id=1", "id 1", "producto: 1", "{"id": 1}", "?id=1")
            val extractedId = directId
                ?: Regex("""(?i)\b(?:id|producto|product)[\s:=]+(\d+)\b""").find(cleanContent)?.groupValues?.get(1)?.toLongOrNull()
                ?: Regex("""\"id\"\s*:\s*(\d+)""").find(cleanContent)?.groupValues?.get(1)?.toLongOrNull()
                ?: Regex("""[?&]id=(\d+)""").find(cleanContent)?.groupValues?.get(1)?.toLongOrNull()

            val products = _uiState.value.allProducts

            // Buscar en memoria primero
            var foundProduct = extractedId?.let { id -> products.find { it.id == id } }

            // Si no está en memoria, consultar repositorio por ID
            if (foundProduct == null && extractedId != null) {
                foundProduct = productRepository.getProductById(extractedId)
            }

            // 3. Fallback: Búsqueda por código de repuesto (ej: "FIL-ACE-001")
            if (foundProduct == null) {
                foundProduct = products.find { it.code.equals(cleanContent, ignoreCase = true) }
            }
            if (foundProduct == null) {
                foundProduct = productRepository.getProductByCode(cleanContent)
            }

            // Fallback general en catálogo
            if (foundProduct == null) {
                val all = productRepository.getAllProducts().firstOrNull() ?: emptyList()
                foundProduct = (extractedId?.let { id -> all.find { it.id == id } })
                    ?: all.find { it.code.equals(cleanContent, ignoreCase = true) }
            }

            if (foundProduct != null) {
                onProductSelected(foundProduct)
            } else {
                _uiState.update {
                    it.copy(
                        errorMessage = "No se encontró ningún repuesto con ID o código '$cleanContent'."
                    )
                }
            }
        }
    }


    fun onMovementTypeSelected(type: MovementTypeDomain) {
        val defaultReason = when (type) {
            MovementTypeDomain.ENTRY -> "Recepción de pedido de proveedor"
            MovementTypeDomain.SALE -> "Venta a cliente en mostrador"
            MovementTypeDomain.SERVICE_CONSUMPTION -> "Insumo utilizado en taller mecánico"
            MovementTypeDomain.ADJUSTMENT -> "Ajuste por inventario físico"
            MovementTypeDomain.RETURN -> "Devolución de repuesto por cliente"
        }
        _uiState.update {
            it.copy(
                movementType = type,
                reason = defaultReason,
                errorMessage = null
            )
        }
    }

    fun onQuantityChanged(qty: Int) {
        if (qty > 0) {
            _uiState.update { it.copy(quantity = qty, errorMessage = null) }
        }
    }

    fun onReasonChanged(reason: String) {
        _uiState.update { it.copy(reason = reason) }
    }

    fun onObservationChanged(obs: String) {
        _uiState.update { it.copy(observation = obs) }
    }

    fun submitMovement(userId: String, onSuccess: () -> Unit) {
        val state = _uiState.value
        val productId = state.selectedProductId ?: return
        val product = state.selectedProduct ?: return

        // Validación estricta: impedimento de stock negativo
        if (state.isNegativeStockRisk) {
            _uiState.update {
                it.copy(
                    errorMessage = "Acción bloqueada: No se puede generar inventario negativo. Stock actual: ${product.currentStock}, intentando retirar: ${state.quantity}."
                )
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val result = registerMovementUseCase(
                productId = productId,
                movementType = state.movementType,
                quantity = state.quantity,
                reason = state.reason,
                observation = state.observation.ifBlank { null },
                userId = userId
            )

            result.onSuccess {
                _uiState.update { it.copy(isLoading = false, isSuccess = true) }

                // Verificar si se alcanzó stock crítico o sin stock para disparar notificación local
                val projected = state.projectedStock ?: 0
                if (projected <= product.minStock) {
                    StockNotificationHelper.showCriticalStockAlert(
                        context = getApplication(),
                        productId = product.id,
                        productName = product.name,
                        currentStock = projected,
                        minStock = product.minStock
                    )
                }

                onSuccess()
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = error.localizedMessage ?: "Error al registrar movimiento"
                    )
                }
            }
        }
    }
}
