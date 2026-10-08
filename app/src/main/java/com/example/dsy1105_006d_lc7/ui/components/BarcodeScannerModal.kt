package com.example.dsy1105_006d_lc7.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

/**
 * Componente modal de escáner de códigos de barra / QR automotriz.
 * Ofrece visor animado con simulación de cámara, detección de códigos de prueba y entrada rápida.
 */
@Composable
fun BarcodeScannerModal(
    onDismiss: () -> Unit,
    onCodeScanned: (String) -> Unit
) {
    // Animación de haz láser de escaneo
    val infiniteTransition = rememberInfiniteTransition(label = "scanner")
    val laserOffsetY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 180f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "laser"
    )

    var manualCodeInput by remember { mutableStateOf("") }

    val presetCodes = listOf(
        "FIL-ACE-001" to "Mann Filter Aceite",
        "ACE-5W30-001" to "Mobil 1 5W-30",
        "PAS-FRE-001" to "Pastillas Bosch",
        "BUJ-NGK-001" to "Bujías NGK"
    )

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Escáner de Repuestos",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Cerrar")
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // ── Visor de cámara simulado ──
                Box(
                    modifier = Modifier
                        .size(240.dp, 200.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF10141C))
                        .border(2.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    // Guías de encuadre de esquinas
                    Icon(
                        Icons.Default.QrCodeScanner,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.3f),
                        modifier = Modifier.size(90.dp)
                    )

                    // Línea láser animada de escaneo
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.85f)
                            .height(2.dp)
                            .offset(y = (laserOffsetY - 90).dp)
                            .background(Color(0xFFFF3B30))
                    )

                    Text(
                        text = "Alinee el código de barras o QR",
                        color = Color.White.copy(alpha = 0.7f),
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Códigos de prueba de taller:",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Chips rápidos de prueba
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    presetCodes.take(2).forEach { (code, label) ->
                        OutlinedButton(
                            onClick = {
                                onCodeScanned(code)
                                onDismiss()
                            },
                            modifier = Modifier.weight(1f),
                            contentPadding = PaddingValues(horizontal = 4.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = code,
                                style = MaterialTheme.typography.labelSmall,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    presetCodes.drop(2).forEach { (code, label) ->
                        OutlinedButton(
                            onClick = {
                                onCodeScanned(code)
                                onDismiss()
                            },
                            modifier = Modifier.weight(1f),
                            contentPadding = PaddingValues(horizontal = 4.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = code,
                                style = MaterialTheme.typography.labelSmall,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Entrada manual
                OutlinedTextField(
                    value = manualCodeInput,
                    onValueChange = { manualCodeInput = it },
                    label = { Text("O ingresar código manual") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = {
                        if (manualCodeInput.isNotBlank()) {
                            IconButton(onClick = {
                                onCodeScanned(manualCodeInput.trim())
                                onDismiss()
                            }) {
                                Icon(Icons.Default.Check, contentDescription = "Aplicar")
                            }
                        }
                    }
                )

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = {
                        if (manualCodeInput.isNotBlank()) {
                            onCodeScanned(manualCodeInput.trim())
                        } else {
                            // Por defecto el primer preset
                            onCodeScanned("FIL-ACE-001")
                        }
                        onDismiss()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.QrCodeScanner, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Capturar Código")
                }
            }
        }
    }
}
