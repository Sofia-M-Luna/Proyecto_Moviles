package com.example.dsy1105_006d_lc7.ui.screens.movement

import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.dsy1105_006d_lc7.domain.model.MovementTypeDomain
import com.example.dsy1105_006d_lc7.domain.model.UserRole
import com.example.dsy1105_006d_lc7.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterMovementScreen(
    currentUsername: String,
    userRole: UserRole,
    onNavigateBack: () -> Unit,
    viewModel: MovementViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val scrollState = rememberScrollState()

    var productExpanded by remember { mutableStateOf(false) }
    var showConfirmDialog by remember { mutableStateOf(false) }

    val presetReasons = when (state.movementType) {
        MovementTypeDomain.ENTRY -> listOf("Recepción proveedor", "Compra local urgente", "Devolución a bodega")
        MovementTypeDomain.SALE -> listOf("Venta a cliente", "Venta con factura", "Venta taller externo")
        MovementTypeDomain.SERVICE_CONSUMPTION -> listOf("Mantención vehículo", "Afinamiento motor", "Reparación frenos")
        MovementTypeDomain.ADJUSTMENT -> listOf("Inventario físico", "Merma o deterioro", "Corrección conteo")
        MovementTypeDomain.RETURN -> listOf("Devolución por garantía", "Producto equivocado", "Cambio mano de obra")
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Registrar Movimiento", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
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
            // Error banner si existe
            state.errorMessage?.let { error ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Error,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = error,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }

            // ── Selector de Producto ──
            Text(
                text = "1. Seleccionar Repuesto",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            ExposedDropdownMenuBox(
                expanded = productExpanded,
                onExpandedChange = { productExpanded = !productExpanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = state.selectedProduct?.let { "${it.code} - ${it.name}" }
                        ?: "Seleccione un repuesto",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Repuesto de Taller") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = productExpanded) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = productExpanded,
                    onDismissRequest = { productExpanded = false }
                ) {
                    state.allProducts.forEach { product ->
                        DropdownMenuItem(
                            text = {
                                Column {
                                    Text(
                                        "${product.code} - ${product.name}",
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        "Stock actual: ${product.currentStock} ${product.unitMeasure}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            },
                            onClick = {
                                viewModel.onProductSelected(product)
                                productExpanded = false
                            }
                        )
                    }
                }
            }

            // ── Selector de Tipo de Movimiento ──
            Text(
                text = "2. Tipo de Movimiento",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = state.movementType == MovementTypeDomain.SALE,
                        onClick = { viewModel.onMovementTypeSelected(MovementTypeDomain.SALE) },
                        label = { Text("Venta") },
                        leadingIcon = {
                            if (state.movementType == MovementTypeDomain.SALE) {
                                Icon(Icons.Default.Check, contentDescription = null)
                            }
                        },
                        modifier = Modifier.weight(1f)
                    )

                    FilterChip(
                        selected = state.movementType == MovementTypeDomain.SERVICE_CONSUMPTION,
                        onClick = { viewModel.onMovementTypeSelected(MovementTypeDomain.SERVICE_CONSUMPTION) },
                        label = { Text("Consumo Taller") },
                        leadingIcon = {
                            if (state.movementType == MovementTypeDomain.SERVICE_CONSUMPTION) {
                                Icon(Icons.Default.Check, contentDescription = null)
                            }
                        },
                        modifier = Modifier.weight(1.3f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = state.movementType == MovementTypeDomain.ENTRY,
                        onClick = { viewModel.onMovementTypeSelected(MovementTypeDomain.ENTRY) },
                        label = { Text("Entrada / Compra") },
                        modifier = Modifier.weight(1.2f)
                    )

                    FilterChip(
                        selected = state.movementType == MovementTypeDomain.ADJUSTMENT,
                        onClick = { viewModel.onMovementTypeSelected(MovementTypeDomain.ADJUSTMENT) },
                        label = { Text("Ajuste") },
                        modifier = Modifier.weight(1f)
                    )

                    FilterChip(
                        selected = state.movementType == MovementTypeDomain.RETURN,
                        onClick = { viewModel.onMovementTypeSelected(MovementTypeDomain.RETURN) },
                        label = { Text("Devolución") },
                        modifier = Modifier.weight(1.1f)
                    )
                }
            }

            // ── Cantidad con Steppers ──
            Text(
                text = "3. Cantidad",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        FilledTonalIconButton(
                            onClick = {
                                if (state.quantity > 1) viewModel.onQuantityChanged(state.quantity - 1)
                            },
                            enabled = state.quantity > 1
                        ) {
                            Icon(Icons.Default.Remove, contentDescription = "Menos 1")
                        }

                        Text(
                            text = "${state.quantity}",
                            style = MaterialTheme.typography.displayMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 24.dp)
                        )

                        FilledTonalIconButton(
                            onClick = { viewModel.onQuantityChanged(state.quantity + 1) }
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Más 1")
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Botones rápidos de incremento
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedButton(
                            onClick = { viewModel.onQuantityChanged(state.quantity + 5) },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("+5")
                        }
                        OutlinedButton(
                            onClick = { viewModel.onQuantityChanged(state.quantity + 10) },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("+10")
                        }
                        OutlinedButton(
                            onClick = { viewModel.onQuantityChanged(1) },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Reset")
                        }
                    }
                }
            }

            // ── Proyección de Stock en Tiempo Real ──
            state.selectedProduct?.let { product ->
                val projected = state.projectedStock ?: product.currentStock
                val isNegative = state.isNegativeStockRisk

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isNegative) MaterialTheme.colorScheme.errorContainer
                        else if (projected <= product.minStock) Color(0xFFFFF3CD)
                        else MaterialTheme.colorScheme.surface
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Balance de Inventario Resultante",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = if (isNegative) MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    "Stock Actual: ${product.currentStock}",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    "Mínimo configurado: ${product.minStock}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Icon(Icons.Default.ArrowForward, contentDescription = null)
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "Resultante: $projected",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = when {
                                        isNegative -> StockCritical
                                        projected == 0 -> StockCritical
                                        projected <= product.minStock -> StockLow
                                        else -> StockOptimal
                                    }
                                )
                                Text(
                                    text = if (isNegative) "¡NEGATIVO IMPEDIDO!"
                                    else if (projected <= product.minStock) "Bajo nivel mínimo"
                                    else "Nivel adecuado",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isNegative) StockCritical else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            // ── Motivo / Razón ──
            Text(
                text = "4. Motivo y Observación",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            OutlinedTextField(
                value = state.reason,
                onValueChange = viewModel::onReasonChanged,
                label = { Text("Motivo del movimiento *") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // Chips rápidos de motivos
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                presetReasons.forEach { preset ->
                    AssistChip(
                        onClick = { viewModel.onReasonChanged(preset) },
                        label = { Text(preset, style = MaterialTheme.typography.labelSmall) }
                    )
                }
            }

            OutlinedTextField(
                value = state.observation,
                onValueChange = viewModel::onObservationChanged,
                label = { Text("Observación adicional (Opcional, ej: Patente auto, N° OT)") },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 2
            )

            Spacer(modifier = Modifier.height(16.dp))

            // ── Botón Registrar ──
            Button(
                onClick = { showConfirmDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                enabled = !state.isLoading && !state.isNegativeStockRisk && state.selectedProductId != null
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Icon(Icons.Default.CheckCircle, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Confirmar y Registrar", fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }

    // ── Diálogo de Confirmación Atómica ──
    if (showConfirmDialog) {
        val prod = state.selectedProduct
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            title = { Text("Confirmar Operación de Inventario") },
            text = {
                Text(
                    "¿Registrar ${state.movementType.displayName} de ${state.quantity} un. para '${prod?.name}'?\n\n" +
                            "• Stock anterior: ${prod?.currentStock}\n" +
                            "• Stock resultante: ${state.projectedStock}\n" +
                            "• Responsable: $currentUsername (${userRole.displayName})"
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showConfirmDialog = false
                        viewModel.submitMovement(
                            userId = currentUsername,
                            onSuccess = onNavigateBack
                        )
                    }
                ) {
                    Text("Confirmar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}
