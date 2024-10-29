package dev.tekofx.biblioteques.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import dev.tekofx.biblioteques.R
import dev.tekofx.biblioteques.navigation.NavScreen
import dev.tekofx.biblioteques.ui.IconResource

sealed class ItemsBottomNav(
    val icon: IconResource,
    val title: String,
    val path: String
) {
    object Item1 : ItemsBottomNav(
        IconResource.fromImageVector(Icons.Filled.Home),
        "Biblioteques",
        NavScreen.HomeScreen.name
    )

    object Item2 : ItemsBottomNav(
        IconResource.fromDrawableResource(R.drawable.book),
        "Llibres",
        NavScreen.BottomNavigationItems.name
    )


}