package com.example.dsy1105_006d_lc7.ui.screens.location

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dsy1105_006d_lc7.domain.model.Location
import com.example.dsy1105_006d_lc7.domain.model.Product
import com.example.dsy1105_006d_lc7.domain.repository.LocationRepository
import com.example.dsy1105_006d_lc7.domain.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LocationWithProducts(
    val location: Location,
    val products: List<Product>
)

data class LocationUiState(
    val isLoading: Boolean = true,
    val locations: List<LocationWithProducts> = emptyList(),
    val zones: List<String> = emptyList(),
    val selectedZone: String = "Todas",
    val errorMessage: String? = null
)

@HiltViewModel
class LocationViewModel @Inject constructor(
    private val locationRepository: LocationRepository,
    private val productRepository: ProductRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LocationUiState())
    val uiState: StateFlow<LocationUiState> = _uiState.asStateFlow()

    init {
        loadLocations()
    }

    private fun loadLocations() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            combine(
                locationRepository.getAllLocations(),
                productRepository.getAllProducts()
            ) { locs, products ->
                val grouped = locs.map { loc ->
                    val prodsInLocation = products.filter { it.locationId == loc.id }
                    LocationWithProducts(location = loc, products = prodsInLocation)
                }

                val allZones = listOf("Todas") + locs.map { it.zone }.distinct().sorted()

                LocationUiState(
                    isLoading = false,
                    locations = grouped,
                    zones = allZones,
                    selectedZone = _uiState.value.selectedZone
                )
            }.collect { state ->
                _uiState.value = state
            }
        }
    }

    fun onZoneSelected(zone: String) {
        _uiState.update { it.copy(selectedZone = zone) }
    }
}
