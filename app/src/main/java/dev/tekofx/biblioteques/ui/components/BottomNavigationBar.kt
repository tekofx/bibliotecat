package dev.tekofx.biblioteques.ui.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import dev.tekofx.biblioteques.navigation.BottomNavigationItems
import dev.tekofx.biblioteques.navigation.currentRoute

@Composable
fun BottomNavigationBar(
    navHostController: NavHostController,
    onMenuClick: () -> Unit
) {
    val items = listOf(
        BottomNavigationItems.Libraries,
        BottomNavigationItems.Catalog,
    )

    BottomAppBar(
        contentPadding = PaddingValues(0.dp),
        modifier = Modifier
            .padding(0.dp)
            .height(60.dp)
    ) {
        IconButton(onClick = onMenuClick) {
            Icon(
                Icons.Outlined.Menu,
                contentDescription = ""
            )
        }
        NavigationBar(
            modifier = Modifier.fillMaxWidth()
        ) {
            items.forEach { item ->
                val selected = currentRoute(navController = navHostController) == item.route
                NavigationBarItem(
                    modifier = Modifier.height(30.dp),
                    selected = selected,
                    onClick = {
                        if (!selected) { // Avoid navigating to the same destination
                            navHostController.navigate(item.route) {
                                popUpTo(navHostController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                restoreState = true
                            }
                        }
                    },
                    icon = {
                        if (selected) {

                            Icon(
                                item.selectedIcon.asPainterResource(),
                                contentDescription = item.title
                            )
                        } else {
                            Icon(
                                item.unselectedIcon.asPainterResource(),
                                contentDescription = item.title
                            )
                        }
                    },
                    label = {
                        Text(
                            text = item.title,
                        )
                    }
                )
            }
        }
    }
}
