package com.example.dsy1105_006d_lc7.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat

/**
 * Helper utilitario para verificar si la aplicación dispone del permiso de cámara.
 * Adaptado desde el módulo de lector QR de camara2026.
 */
object CameraPermissionHelper {
    fun hasCameraPermission(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }
}
