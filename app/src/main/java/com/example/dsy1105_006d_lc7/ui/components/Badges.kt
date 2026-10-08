package com.example.dsy1105_006d_lc7.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.dsy1105_006d_lc7.domain.model.MovementTypeDomain
import com.example.dsy1105_006d_lc7.domain.model.ProductStatusDomain
import com.example.dsy1105_006d_lc7.ui.theme.*

/**
 * Badge para mostrar el estado del stock de un producto.
 */
@Composable
fun StockStatusBadge(
    status: ProductStatusDomain,
    modifier: Modifier = Modifier
) {
    val (text, color) = when (status) {
        ProductStatusDomain.AVAILABLE, ProductStatusDomain.ACTIVE -> "Disponible" to StockNormal
        ProductStatusDomain.LOW_STOCK -> "Stock Bajo" to StockLow
        ProductStatusDomain.OUT_OF_STOCK -> "Sin Stock" to StockCritical
        ProductStatusDomain.INACTIVE -> "Inactivo" to Color.Gray
        ProductStatusDomain.DISCONTINUED -> "Descontinuado" to Color.DarkGray
    }

    val animatedColor by animateColorAsState(
        targetValue = color,
        animationSpec = tween(300),
        label = "badge_color"
    )

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(animatedColor.copy(alpha = 0.15f))
            .padding(horizontal = 10.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = animatedColor
        )
    }
}

/**
 * Badge para mostrar el tipo de movimiento de inventario.
 */
@Composable
fun MovementTypeBadge(
    type: MovementTypeDomain,
    modifier: Modifier = Modifier
) {
    val (text, color) = when (type) {
        MovementTypeDomain.ENTRY -> "Entrada" to BadgeEntry
        MovementTypeDomain.SALE -> "Venta" to BadgeSale
        MovementTypeDomain.SERVICE_CONSUMPTION -> "Consumo" to BadgeConsumption
        MovementTypeDomain.ADJUSTMENT -> "Ajuste" to BadgeAdjustment
        MovementTypeDomain.RETURN -> "Devolución" to BadgeReturn
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(color.copy(alpha = 0.15f))
            .padding(horizontal = 10.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}

/**
 * Barra de progreso de stock con colores semánticos.
 */
@Composable
fun StockLevelBar(
    currentStock: Int,
    minStock: Int,
    maxDisplay: Int = minStock * 3,
    modifier: Modifier = Modifier
) {
    val ratio = if (maxDisplay > 0) (currentStock.toFloat() / maxDisplay).coerceIn(0f, 1f) else 0f
    val barColor = when {
        currentStock == 0 -> StockCritical
        currentStock <= minStock -> StockLow
        else -> StockNormal
    }

    val animatedColor by animateColorAsState(
        targetValue = barColor,
        animationSpec = tween(500),
        label = "stock_bar"
    )

    Column(modifier = modifier) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(ratio)
                    .clip(RoundedCornerShape(3.dp))
                    .background(animatedColor)
            )
        }
        Spacer(modifier = Modifier.height(2.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "$currentStock uds",
                style = MaterialTheme.typography.labelSmall,
                color = animatedColor
            )
            Text(
                text = "Mín: $minStock",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
