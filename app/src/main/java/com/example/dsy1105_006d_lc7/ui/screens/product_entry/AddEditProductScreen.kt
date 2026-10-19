package com.example.dsy1105_006d_lc7.ui.screens.product_entry

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.dsy1105_006d_lc7.domain.model.UserRole
import com.example.dsy1105_006d_lc7.ui.components.BarcodeScannerModal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditProductScreen(
    userRole: UserRole,
    onNavigateBack: () -> Unit,
    viewModel: AddEditProductViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val scrollState = rememberScrollState()

    var showScanner by remember { mutableStateOf(false) }
    var categoryExpanded by remember { mutableStateOf(false) }
    var unitExpanded by remember { mutableStateOf(false) }
    var supplierExpanded by remember { mutableStateOf(false) }
    var locationExpanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (state.isEditing) "Editar Repuesto" else "Nuevo Repuesto",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    TextButton(
                        onClick = { viewModel.saveProduct(onNavigateBack) },
                        enabled = !state.isLoading
                    ) {
                        Text("Guardar", fontWeight = FontWeight.Bold)
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Mensaje de error si existe
            state.errorMessage?.let { error ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.padding(12.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            // ── Código de Producto + Botón Escáner ──
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = state.code,
                    onValueChange = viewModel::onCodeChange,
                    label = { Text("Código de Parte / Barras *") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    leadingIcon = {
                        Icon(Icons.Default.QrCode, contentDescription = null)
                    },
                    trailingIcon = {
                        if (state.code.isNotBlank()) {
                            IconButton(onClick = { viewModel.onQrCodeScanned(state.code) }) {
                                Icon(
                                    Icons.Default.Search,
                                    contentDescription = "Buscar datos del repuesto",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                )
                FilledTonalIconButton(
                    onClick = { showScanner = true },
                    modifier = Modifier.size(56.dp)
                ) {
                    Icon(Icons.Default.QrCodeScanner, contentDescription = "Escanear")
                }
            }

            // ── Nombre y Descripción ──
            OutlinedTextField(
                value = state.name,
                onValueChange = viewModel::onNameChange,
                label = { Text("Nombre del Producto / Repuesto *") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                leadingIcon = {
                    Icon(Icons.Default.Build, contentDescription = null)
                }
            )

            OutlinedTextField(
                value = state.description,
                onValueChange = viewModel::onDescriptionChange,
                label = { Text("Descripción / Compatibilidad Vehículo") },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 3
            )

            // ── Marca y Categoría ──
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = state.brand,
                    onValueChange = viewModel::onBrandChange,
                    label = { Text("Marca (ej: Bosch)") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )

                // Categoría Dropdown
                ExposedDropdownMenuBox(
                    expanded = categoryExpanded,
                    onExpandedChange = { categoryExpanded = !categoryExpanded },
                    modifier = Modifier.weight(1f)
                ) {
                    OutlinedTextField(
                        value = state.category,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Categoría") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) },
                        modifier = Modifier.menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = categoryExpanded,
                        onDismissRequest = { categoryExpanded = false }
                    ) {
                        state.categories.forEach { cat ->
                            DropdownMenuItem(
                                text = { Text(cat) },
                                onClick = {
                                    viewModel.onCategoryChange(cat)
                                    categoryExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            // ── Unidad de Medida ──
            ExposedDropdownMenuBox(
                expanded = unitExpanded,
                onExpandedChange = { unitExpanded = !unitExpanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = state.unitMeasure,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Unidad de Medida") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = unitExpanded) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = unitExpanded,
                    onDismissRequest = { unitExpanded = false }
                ) {
                    state.units.forEach { unit ->
                        DropdownMenuItem(
                            text = { Text(unit) },
                            onClick = {
                                viewModel.onUnitMeasureChange(unit)
                                unitExpanded = false
                            }
                        )
                    }
                }
            }

            // ── Parámetros de Stock ──
            Text(
                text = "Control de Inventario",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = state.currentStock,
                    onValueChange = viewModel::onCurrentStockChange,
                    label = { Text("Stock Actual") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )

                OutlinedTextField(
                    value = state.minStock,
                    onValueChange = viewModel::onMinStockChange,
                    label = { Text("Stock Mínimo") },
                    supportingText = {
                        if (!userRole.canModifyMinStock) Text("Solo Propietario")
                    },
                    enabled = userRole.canModifyMinStock,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
            }

            OutlinedTextField(
                value = state.suggestedReplenishment,
                onValueChange = viewModel::onSuggestedReplenishmentChange,
                label = { Text("Cantidad Sugerida de Reposición") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // ── Precios de Referencia ──
            Text(
                text = "Precios de Referencia (CLP)",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = state.referenceCostPrice,
                    onValueChange = viewModel::onCostPriceChange,
                    label = { Text("Precio Costo") },
                    prefix = { Text("$") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )

                OutlinedTextField(
                    value = state.referenceSalePrice,
                    onValueChange = viewModel::onSalePriceChange,
                    label = { Text("Precio Venta") },
                    prefix = { Text("$") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
            }

            // ── Logística (Proveedor y Ubicación) ──
            Text(
                text = "Logística y Almacenamiento",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            // Selector Proveedor
            ExposedDropdownMenuBox(
                expanded = supplierExpanded,
                onExpandedChange = { supplierExpanded = !supplierExpanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                val selectedSupplier = state.suppliers.find { it.id == state.supplierId }
                OutlinedTextField(
                    value = selectedSupplier?.companyName ?: "Seleccionar Proveedor",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Proveedor Principal") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = supplierExpanded) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = supplierExpanded,
                    onDismissRequest = { supplierExpanded = false }
                ) {
                    state.suppliers.forEach { sup ->
                        DropdownMenuItem(
                            text = { Text("${sup.companyName} (${sup.code})") },
                            onClick = {
                                viewModel.onSupplierChange(sup.id)
                                supplierExpanded = false
                            }
                        )
                    }
                }
            }

            // Selector Ubicación
            ExposedDropdownMenuBox(
                expanded = locationExpanded,
                onExpandedChange = { locationExpanded = !locationExpanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                val selectedLoc = state.locations.find { it.id == state.locationId }
                OutlinedTextField(
                    value = selectedLoc?.let { "${it.code} (${it.zone})" } ?: "Sin ubicación",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Ubicación en Bodega") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = locationExpanded) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = locationExpanded,
                    onDismissRequest = { locationExpanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Sin ubicación asignada") },
                        onClick = {
                            viewModel.onLocationChange(null)
                            locationExpanded = false
                        }
                    )
                    state.locations.forEach { loc ->
                        DropdownMenuItem(
                            text = { Text("${loc.code} - ${loc.zone} (${loc.shelf})") },
                            onClick = {
                                viewModel.onLocationChange(loc.id)
                                locationExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Botón Guardar
            Button(
                onClick = { viewModel.saveProduct(onNavigateBack) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                enabled = !state.isLoading
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Icon(Icons.Default.Save, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Guardar Repuesto", fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }

    // Modal de Escáner
    if (showScanner) {
        BarcodeScannerModal(
            onDismiss = { showScanner = false },
            onCodeScanned = { scannedCode ->
                viewModel.onQrCodeScanned(scannedCode)
            }
        )
    }
}
