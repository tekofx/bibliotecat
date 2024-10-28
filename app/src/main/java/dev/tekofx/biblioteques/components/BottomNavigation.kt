package dev.tekofx.biblioteques.components

import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import dev.tekofx.biblioteques.model.ItemsBottomNav
import dev.tekofx.biblioteques.navigation.currentRoute

@Composable
fun BottomNavigation(navHostController: NavHostController) {
    val items = listOf(
        ItemsBottomNav.Item1,
        ItemsBottomNav.Item2
    )

    BottomAppBar {
        NavigationBar {
            items.forEach { item ->
                val selected = currentRoute(navController = navHostController) == item.path
                NavigationBarItem(
                    selected = selected,
                    onClick = { navHostController.navigate(item.path) },
                    icon = { Icon(imageVector = item.icon, contentDescription = item.title) },
                    label = {
                        Text(text = item.title)
                    }
                )


            }
        }
    }

}