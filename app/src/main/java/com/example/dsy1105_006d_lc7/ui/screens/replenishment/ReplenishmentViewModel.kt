package com.example.dsy1105_006d_lc7.ui.screens.replenishment

import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dsy1105_006d_lc7.domain.model.ReplenishmentItem
import com.example.dsy1105_006d_lc7.domain.model.ReplenishmentReport
import com.example.dsy1105_006d_lc7.domain.model.Supplier
import com.example.dsy1105_006d_lc7.domain.repository.SupplierRepository
import com.example.dsy1105_006d_lc7.domain.usecase.BuildSupplierEmailIntentUseCase
import com.example.dsy1105_006d_lc7.domain.usecase.GetLowStockProductsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ReplenishmentUiState(
    val isLoading: Boolean = true,
    val reports: List<ReplenishmentReport> = emptyList(),
    val totalCriticalProducts: Int = 0,
    val grandTotalEstimatedCost: Double = 0.0,
    val errorMessage: String? = null
)

@HiltViewModel
class ReplenishmentViewModel @Inject constructor(
    private val getLowStockProductsUseCase: GetLowStockProductsUseCase,
    private val supplierRepository: SupplierRepository,
    private val buildSupplierEmailIntentUseCase: BuildSupplierEmailIntentUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReplenishmentUiState())
    val uiState: StateFlow<ReplenishmentUiState> = _uiState.asStateFlow()

    init {
        loadReplenishmentReports()
    }

    fun loadReplenishmentReports() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                combine(
                    getLowStockProductsUseCase(),
                    supplierRepository.getAllSuppliers()
                ) { lowStockProducts, suppliers ->
                    val supplierMap = suppliers.associateBy { it.id }

                    // Agrupar productos con stock crítico/bajo por proveedor
                    val groupedBySupplier = lowStockProducts.groupBy { it.supplierId ?: 1L }

                    val reportsList = groupedBySupplier.map { (supplierId, products) ->
                        val supplier = supplierMap[supplierId] ?: Supplier(
                            id = supplierId,
                            code = "PROV-GEN",
                            companyName = "Proveedor Desconocido",
                            contactName = "Contacto",
                            email = "proveedor@taller.cl",
                            phone = null,
                            suppliedCategories = emptyList(),
                            isActive = true
                        )

                        val items = products.map { prod ->
                            // Cálculo de cantidad sugerida: TargetStock - CurrentStock
                            val targetStock = if (prod.suggestedReplenishment > 0) {
                                prod.suggestedReplenishment
                            } else {
                                prod.minStock * 2
                            }
                            val needed = maxOf(targetStock - prod.currentStock, 1)
                            val cost = needed * prod.referenceCostPrice

                            ReplenishmentItem(
                                product = prod,
                                quantityNeeded = needed,
                                estimatedCost = cost
                            )
                        }

                        ReplenishmentReport(
                            supplier = supplier,
                            items = items
                        )
                    }

                    val totalProds = lowStockProducts.size
                    val grandCost = reportsList.sumOf { it.estimatedTotalCost }

                    ReplenishmentUiState(
                        isLoading = false,
                        reports = reportsList,
                        totalCriticalProducts = totalProds,
                        grandTotalEstimatedCost = grandCost
                    )
                }.collect { newState ->
                    _uiState.value = newState
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Error al calcular reposición: ${e.localizedMessage}"
                    )
                }
            }
        }
    }

    fun createEmailIntent(report: ReplenishmentReport): Intent {
        return buildSupplierEmailIntentUseCase(
            supplier = report.supplier,
            items = report.items
        )
    }
}
