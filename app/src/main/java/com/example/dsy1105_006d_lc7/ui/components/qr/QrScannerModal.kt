package com.example.dsy1105_006d_lc7.ui.components.qr

import android.Manifest
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.dsy1105_006d_lc7.util.CameraPermissionHelper

/**
 * Diálogo Modal interactivo para escaneo de códigos QR y de barras.
 * Integra solicitud de permisos en tiempo real, visor de cámara CameraX + ML Kit,
 * guías visuales de enfoque y fallback de prueba para emuladores.
 */
@Composable
fun QrScannerModal(
    onDismiss: () -> Unit,
    onQrCodeScanned: (String) -> Unit
) {
    val context = LocalContext.current
    var hasPermission by remember {
        mutableStateOf(CameraPermissionHelper.hasCameraPermission(context))
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasPermission = isGranted
        if (!isGranted) {
            Toast.makeText(
                context,
                "Se requiere permiso de cámara para escanear el código QR",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    var manualInput by remember { mutableStateOf("") }

    // Animación de haz láser para la guía de encuadre
    val infiniteTransition = rememberInfiniteTransition(label = "laser_transition")
    val laserOffsetY by infiniteTransition.animateFloat(
        initialValue = -80f,
        targetValue = 80f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "laser_offset"
    )

    // IDs de repuestos de prueba para validación rápida
    val quickTestIds = listOf(
        "1" to "ID 1: Filtro Aceite",
        "2" to "ID 2: Aceite 5W-30",
        "3" to "ID 3: Pastillas Freno",
        "4" to "ID 4: Bujía NGK"
    )

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .padding(vertical = 16.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Cabecera del Diálogo
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.QrCodeScanner,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Escanear QR de Repuesto",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Cerrar")
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (!hasPermission) {
                    // Vista cuando no hay permisos concedidos
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                Icons.Default.CameraAlt,
                                contentDescription = null,
                                modifier = Modifier.size(48.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                "Permiso de Cámara Requerido",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                "Para identificar el repuesto mediante código QR, necesitamos acceso a la cámara del dispositivo.",
                                style = MaterialTheme.typography.bodySmall,
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = {
                                    permissionLauncher.launch(Manifest.permission.CAMERA)
                                }
                            ) {
                                Text("Conceder Permiso")
                            }
                        }
                    }
                } else {
                    // Visor de Cámara Activo con CameraX y ML Kit
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(240.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.Black),
                        contentAlignment = Alignment.Center
                    ) {
                        QrScanner(
                            onQrCodeScanned = { scannedContent ->
                                Toast.makeText(
                                    context,
                                    "QR detectado: $scannedContent",
                                    Toast.LENGTH_SHORT
                                ).show()
                                onQrCodeScanned(scannedContent)
                                onDismiss()
                            },
                            modifier = Modifier.fillMaxSize()
                        )

                        // Marco de encuadre visual
                        Box(
                            modifier = Modifier
                                .size(180.dp)
                                .border(
                                    width = 2.dp,
                                    color = MaterialTheme.colorScheme.primary,
                                    shape = RoundedCornerShape(12.dp)
                                )
                        )

                        // Línea láser roja animada
                        Box(
                            modifier = Modifier
                                .width(170.dp)
                                .height(2.dp)
                                .offset(y = laserOffsetY.dp)
                                .background(Color(0xFFFF3B30))
                        )

                        Text(
                            text = "Alinee el código QR en el centro",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White.copy(alpha = 0.85f),
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(bottom = 8.dp)
                                .background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Accesos Rápidos de Prueba (Ideal para Emuladores o Validación sin QR físico)
                Text(
                    text = "Prueba rápida con IDs de taller (Emulador):",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    quickTestIds.take(2).forEach { (id, label) ->
                        OutlinedButton(
                            onClick = {
                                onQrCodeScanned(id)
                                onDismiss()
                            },
                            modifier = Modifier.weight(1f),
                            contentPadding = PaddingValues(horizontal = 4.dp, vertical = 6.dp)
                        ) {
                            Text(text = label, style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    quickTestIds.drop(2).forEach { (id, label) ->
                        OutlinedButton(
                            onClick = {
                                onQrCodeScanned(id)
                                onDismiss()
                            },
                            modifier = Modifier.weight(1f),
                            contentPadding = PaddingValues(horizontal = 4.dp, vertical = 6.dp)
                        ) {
                            Text(text = label, style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Opción de ingreso manual de ID o Código
                OutlinedTextField(
                    value = manualInput,
                    onValueChange = { manualInput = it },
                    label = { Text("O escribir ID o Código manualmente") },
                    placeholder = { Text("Ej: 1, 2, FIL-ACE-001") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = {
                        if (manualInput.isNotBlank()) {
                            IconButton(onClick = {
                                onQrCodeScanned(manualInput.trim())
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
                        if (manualInput.isNotBlank()) {
                            onQrCodeScanned(manualInput.trim())
                        } else {
                            // Valor por defecto ID 1
                            onQrCodeScanned("1")
                        }
                        onDismiss()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Search, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Buscar Repuesto")
                }
            }
        }
    }
}
