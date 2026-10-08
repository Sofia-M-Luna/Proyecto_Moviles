package com.example.dsy1105_006d_lc7.ui.navigation

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.dsy1105_006d_lc7.domain.model.UserRole
import com.example.dsy1105_006d_lc7.domain.model.UserSession
import com.example.dsy1105_006d_lc7.ui.screens.auth.LoginScreen
import com.example.dsy1105_006d_lc7.ui.screens.location.LocationScreen
import com.example.dsy1105_006d_lc7.ui.screens.movement.RegisterMovementScreen
import com.example.dsy1105_006d_lc7.ui.screens.product_detail.ProductDetailScreen
import com.example.dsy1105_006d_lc7.ui.screens.product_entry.AddEditProductScreen
import com.example.dsy1105_006d_lc7.ui.screens.product_list.ProductListScreen
import com.example.dsy1105_006d_lc7.ui.screens.replenishment.ReplenishmentScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    var currentSession by remember {
        mutableStateOf<UserSession?>(null)
    }

    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route

    val isTopLevelDestination = currentRoute in listOf(
        Screen.ProductList.route,
        Screen.Replenishment.route,
        Screen.Location.route
    )

    var showLogoutDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            if (currentSession != null && isTopLevelDestination) {
                TopAppBar(
                    title = {
                        Column {
                            Text(
                                text = "Taller Puente Alto",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${currentSession?.displayName} • ${currentSession?.role?.displayName}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = { showLogoutDialog = true }) {
                            Icon(Icons.Default.ExitToApp, contentDescription = "Cerrar sesión")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                )
            }
        },
        bottomBar = {
            if (currentSession != null && isTopLevelDestination) {
                NavigationBar(
                    tonalElevation = 6.dp
                ) {
                    NavigationBarItem(
                        selected = currentRoute == Screen.ProductList.route,
                        onClick = {
                            navController.navigate(Screen.ProductList.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(Icons.Default.Inventory2, contentDescription = "Inventario") },
                        label = { Text("Inventario") }
                    )

                    NavigationBarItem(
                        selected = false,
                        onClick = {
                            navController.navigate(Screen.RegisterMovement.createRoute())
                        },
                        icon = { Icon(Icons.Default.SwapHoriz, contentDescription = "Movimiento") },
                        label = { Text("Movimiento") }
                    )

                    NavigationBarItem(
                        selected = currentRoute == Screen.Replenishment.route,
                        onClick = {
                            navController.navigate(Screen.Replenishment.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(Icons.Default.LocalShipping, contentDescription = "Reposición") },
                        label = { Text("Reposición") }
                    )

                    NavigationBarItem(
                        selected = currentRoute == Screen.Location.route,
                        onClick = {
                            navController.navigate(Screen.Location.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(Icons.Default.Place, contentDescription = "Bodega") },
                        label = { Text("Bodega") }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Login.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            // ── LOGIN ──
            composable(Screen.Login.route) {
                LoginScreen(
                    onLoginSuccess = { session ->
                        currentSession = session
                        navController.navigate(Screen.ProductList.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    }
                )
            }

            // ── LISTADO PRODUCTOS ──
            composable(Screen.ProductList.route) {
                val role = currentSession?.role ?: UserRole.OPERATOR
                ProductListScreen(
                    userRole = role,
                    onProductClick = { productId ->
                        navController.navigate(Screen.ProductDetail.createRoute(productId))
                    },
                    onAddProduct = {
                        navController.navigate(Screen.AddEditProduct.createRoute())
                    }
                )
            }

            // ── DETALLE PRODUCTO ──
            composable(
                route = Screen.ProductDetail.route,
                arguments = listOf(
                    navArgument("productId") { type = NavType.LongType }
                )
            ) {
                val role = currentSession?.role ?: UserRole.OPERATOR
                ProductDetailScreen(
                    userRole = role,
                    onNavigateBack = { navController.popBackStack() },
                    onEditProduct = { productId ->
                        navController.navigate(Screen.AddEditProduct.createRoute(productId))
                    },
                    onRegisterMovement = { productId ->
                        navController.navigate(Screen.RegisterMovement.createRoute(productId))
                    }
                )
            }

            // ── AGREGAR / EDITAR PRODUCTO ──
            composable(
                route = Screen.AddEditProduct.route,
                arguments = listOf(
                    navArgument("productId") {
                        type = NavType.LongType
                        defaultValue = -1L
                    }
                )
            ) {
                val role = currentSession?.role ?: UserRole.OPERATOR
                AddEditProductScreen(
                    userRole = role,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            // ── REGISTRAR MOVIMIENTO ──
            composable(
                route = Screen.RegisterMovement.route,
                arguments = listOf(
                    navArgument("productId") {
                        type = NavType.LongType
                        defaultValue = -1L
                    }
                )
            ) {
                val username = currentSession?.username ?: "operador"
                val role = currentSession?.role ?: UserRole.OPERATOR
                RegisterMovementScreen(
                    currentUsername = username,
                    userRole = role,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            // ── REPOSICIÓN ──
            composable(Screen.Replenishment.route) {
                val role = currentSession?.role ?: UserRole.OPERATOR
                ReplenishmentScreen(
                    userRole = role,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            // ── UBICACIONES ──
            composable(Screen.Location.route) {
                LocationScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onProductClick = { productId ->
                        navController.navigate(Screen.ProductDetail.createRoute(productId))
                    }
                )
            }
        }
    }

    // Diálogo confirmación cerrar sesión
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Cerrar Sesión") },
            text = { Text("¿Deseas cerrar la sesión activa del taller?") },
            confirmButton = {
                Button(
                    onClick = {
                        showLogoutDialog = false
                        currentSession = null
                        navController.navigate(Screen.Login.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                ) {
                    Text("Cerrar Sesión")
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}
