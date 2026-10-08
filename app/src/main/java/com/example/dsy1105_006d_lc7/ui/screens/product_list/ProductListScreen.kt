package com.example.dsy1105_006d_lc7.ui.screens.product_list

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.dsy1105_006d_lc7.domain.model.Product
import com.example.dsy1105_006d_lc7.domain.model.UserRole
import com.example.dsy1105_006d_lc7.ui.components.InventorySearchBar
import com.example.dsy1105_006d_lc7.ui.components.ProductCard
import com.example.dsy1105_006d_lc7.ui.components.StatCard
import com.example.dsy1105_006d_lc7.ui.theme.*

/**
 * Pantalla principal de listado de productos con dashboard de estadísticas.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductListScreen(
    userRole: UserRole,
    onProductClick: (Long) -> Unit,
    onAddProduct: () -> Unit,
    viewModel: ProductListViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var showDeleteDialog by remember { mutableStateOf<Product?>(null) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddProduct,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Agregar Producto")
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            // ── Header ──
            item {
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Text(
                        text = "Inventario",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "Gestión de productos automotrices",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // ── Stats Dashboard ──
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard(
                        title = "Total",
                        value = "${state.totalProducts}",
                        icon = {
                            Icon(
                                Icons.Default.Inventory2,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(28.dp)
                            )
                        },
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "Stock Bajo",
                        value = "${state.lowStockCount}",
                        icon = {
                            Icon(
                                Icons.Default.Warning,
                                contentDescription = null,
                                tint = StockLow,
                                modifier = Modifier.size(28.dp)
                            )
                        },
                        valueColor = StockLow,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "Sin Stock",
                        value = "${state.outOfStockCount}",
                        icon = {
                            Icon(
                                Icons.Default.ErrorOutline,
                                contentDescription = null,
                                tint = StockCritical,
                                modifier = Modifier.size(28.dp)
                            )
                        },
                        valueColor = StockCritical,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // ── Search Bar ──
            item {
                Spacer(modifier = Modifier.height(8.dp))
                InventorySearchBar(
                    query = state.searchQuery,
                    onQueryChange = viewModel::onSearchQueryChange
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            // ── Loading ──
            if (state.isLoading) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }

            // ── Empty State ──
            if (!state.isLoading && state.products.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Default.SearchOff,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = if (state.searchQuery.isNotEmpty()) "Sin resultados para \"${state.searchQuery}\""
                            else "No hay productos registrados",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // ── Product List ──
            items(state.products, key = { it.id }) { product ->
                ProductCard(
                    product = product,
                    onClick = { onProductClick(product.id) },
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                )
            }
        }
    }

    // ── Delete Confirmation Dialog ──
    showDeleteDialog?.let { product ->
        AlertDialog(
            onDismissRequest = { showDeleteDialog = null },
            title = { Text("Confirmar Eliminación") },
            text = {
                Text("¿Estás seguro de eliminar '${product.name}'?\nEsta acción no se puede deshacer.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteProduct(product)
                        showDeleteDialog = null
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = null }) {
                    Text("Cancelar")
                }
            }
        )
    }
}
