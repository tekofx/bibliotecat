package dev.tekofx.biblioteques.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Search
import androidx.compose.ui.graphics.vector.ImageVector
import dev.tekofx.biblioteques.navigation.NavScreen

sealed class ItemsBottomNav(
    val icon: ImageVector,
    val title: String,
    val path: String
) {
    object Item1 : ItemsBottomNav(
        Icons.Outlined.Home,
        "Home",
        NavScreen.HomeScreen.name
    )

    object Item2 : ItemsBottomNav(
        Icons.Outlined.Search,
        "Dashboard",
        NavScreen.BottomNavigationItems.name
    )


}