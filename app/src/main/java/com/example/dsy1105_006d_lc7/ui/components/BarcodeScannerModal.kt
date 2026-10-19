package com.example.dsy1105_006d_lc7.ui.components

import androidx.compose.runtime.Composable
import com.example.dsy1105_006d_lc7.ui.components.qr.QrScannerModal

/**
 * Componente modal de escáner de códigos de barra / QR automotriz.
 * Delega al motor unificado CameraX + Google ML Kit (QrScannerModal) adaptado de camara2026.
 */
@Composable
fun BarcodeScannerModal(
    onDismiss: () -> Unit,
    onCodeScanned: (String) -> Unit
) {
    QrScannerModal(
        onDismiss = onDismiss,
        onQrCodeScanned = onCodeScanned
    )
}
