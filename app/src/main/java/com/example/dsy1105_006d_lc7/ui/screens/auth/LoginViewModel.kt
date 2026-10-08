package com.example.dsy1105_006d_lc7.ui.screens.auth

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.dsy1105_006d_lc7.domain.model.UserRole
import com.example.dsy1105_006d_lc7.domain.model.UserSession
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * Estado de la UI de Login.
 */
data class LoginUiState(
    val username: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)

/**
 * Credenciales ficticias para el MVP.
 * Propietario: admin/admin123
 * Operador: operador/op123
 */
private val MOCK_CREDENTIALS = mapOf(
    "admin" to Pair("admin123", UserSession("admin", UserRole.OWNER, "Carlos Propietario")),
    "operador" to Pair("op123", UserSession("operador", UserRole.OPERATOR, "Juan Operador"))
)

/**
 * ViewModel para la pantalla de Login con autenticación mock.
 */
@HiltViewModel
class LoginViewModel @Inject constructor() : ViewModel() {

    var uiState by mutableStateOf(LoginUiState())
        private set

    /** Sesión del usuario autenticado (null si no ha iniciado sesión) */
    var currentSession: UserSession? = null
        private set

    fun onUsernameChange(value: String) {
        uiState = uiState.copy(username = value, error = null)
    }

    fun onPasswordChange(value: String) {
        uiState = uiState.copy(password = value, error = null)
    }

    fun submit(onSuccess: (UserSession) -> Unit) {
        uiState = uiState.copy(isLoading = true, error = null)

        val username = uiState.username.trim().lowercase()
        val credentials = MOCK_CREDENTIALS[username]

        uiState = uiState.copy(isLoading = false)

        if (credentials != null && credentials.first == uiState.password) {
            currentSession = credentials.second
            onSuccess(credentials.second)
        } else {
            uiState = uiState.copy(error = "Credenciales inválidas. Usa: admin/admin123 ó operador/op123")
        }
    }

    fun logout() {
        currentSession = null
        uiState = LoginUiState()
    }
}
