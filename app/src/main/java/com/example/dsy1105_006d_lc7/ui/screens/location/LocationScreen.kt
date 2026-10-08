package com.example.dsy1105_006d_lc7.ui.screens.location

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.dsy1105_006d_lc7.domain.model.LocationStatusDomain
import com.example.dsy1105_006d_lc7.ui.components.StockStatusBadge
import com.example.dsy1105_006d_lc7.ui.theme.StockOptimal
import com.example.dsy1105_006d_lc7.ui.theme.StockWarning

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationScreen(
    onNavigateBack: () -> Unit,
    onProductClick: (Long) -> Unit,
    viewModel: LocationViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    val filteredLocations = remember(state.locations, state.selectedZone) {
        if (state.selectedZone == "Todas") state.locations
        else state.locations.filter { it.location.zone == state.selectedZone }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ubicaciones de Bodega", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { innerPadding ->
        if (state.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // ── Filtro por Zona ──
                item {
                    Column {
                        Text(
                            text = "Zonas del Taller",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(state.zones) { zone ->
                                FilterChip(
                                    selected = zone == state.selectedZone,
                                    onClick = { viewModel.onZoneSelected(zone) },
                                    label = { Text(zone) }
                                )
                            }
                        }
                    }
                }

                // ── Listado de Ubicaciones ──
                items(filteredLocations) { itemLoc ->
                    val loc = itemLoc.location
                    val products = itemLoc.products

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            // Cabecera con código y estado
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Default.Place,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = loc.code,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                // Status Badge
                                val statusColor: Color = when (loc.status) {
                                    LocationStatusDomain.AVAILABLE -> StockOptimal
                                    LocationStatusDomain.OCCUPIED -> MaterialTheme.colorScheme.primary
                                    LocationStatusDomain.REORGANIZING -> StockWarning
                                    LocationStatusDomain.OUT_OF_SERVICE -> MaterialTheme.colorScheme.error
                                }
                                val statusText: String = when (loc.status) {
                                    LocationStatusDomain.AVAILABLE -> "DISPONIBLE"
                                    LocationStatusDomain.OCCUPIED -> "OCUPADA"
                                    LocationStatusDomain.REORGANIZING -> "REORGANIZANDO"
                                    LocationStatusDomain.OUT_OF_SERVICE -> "FUERA DE SERV."
                                }

                                Surface(
                                    color = statusColor.copy(alpha = 0.15f),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = statusText,
                                        color = statusColor,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Coordenadas en bodega
                            Text(
                                text = "${loc.zone} • ${loc.shelf} • ${loc.rack} • Posición: ${loc.position}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                            // Productos almacenados en esta posición
                            if (products.isEmpty()) {
                                Text(
                                    text = "Espacio libre. Sin repuestos asignados.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            } else {
                                Text(
                                    text = "Repuestos en esta ubicación (${products.size}):",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                products.forEach { prod ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(8.dp))
                                            .clickable { onProductClick(prod.id) }
                                            .padding(vertical = 4.dp, horizontal = 4.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = prod.name,
                                                style = MaterialTheme.typography.bodyMedium,
                                                fontWeight = FontWeight.Medium
                                            )
                                            Text(
                                                text = "${prod.code} • ${prod.brand}",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }

                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Text(
                                                text = "${prod.currentStock} ${prod.unitMeasure}",
                                                style = MaterialTheme.typography.bodyMedium,
                                                fontWeight = FontWeight.Bold
                                            )
                                            StockStatusBadge(status = prod.status)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
