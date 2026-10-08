package com.example.dsy1105_006d_lc7.ui.navigation

sealed class Screen(val route: String) {
    data object Login : Screen("login")
    data object ProductList : Screen("product_list")
    data object Replenishment : Screen("replenishment")
    data object Location : Screen("location")

    data object ProductDetail : Screen("product_detail/{productId}") {
        fun createRoute(productId: Long) = "product_detail/$productId"
    }

    data object AddEditProduct : Screen("product_entry?productId={productId}") {
        fun createRoute(productId: Long? = null) =
            if (productId != null) "product_entry?productId=$productId" else "product_entry"
    }

    data object RegisterMovement : Screen("movement?productId={productId}") {
        fun createRoute(productId: Long? = null) =
            if (productId != null) "movement?productId=$productId" else "movement"
    }
}
