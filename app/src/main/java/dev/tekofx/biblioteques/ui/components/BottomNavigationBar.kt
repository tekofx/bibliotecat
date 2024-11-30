package dev.tekofx.biblioteques.ui.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
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
fun BottomNavigationBar(navHostController: NavHostController) {
    val items = listOf(
        BottomNavigationItems.Item1,
        BottomNavigationItems.Item2,
    )

    BottomAppBar(
        contentPadding = PaddingValues(0.dp),
        modifier = Modifier
            .padding(0.dp)
            .height(80.dp)
    ) {
        NavigationBar {
            items.forEach { item ->
                val selected = currentRoute(navController = navHostController) == item.path
                NavigationBarItem(
                    modifier = Modifier.height(30.dp),
                    selected = selected,
                    onClick = {
                        navHostController.navigate(item.path)
                        {
                            popUpTo(navHostController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            restoreState = true
                        }
                    },
                    icon = {
                        Icon(
                            item.icon.asPainterResource(),
                            contentDescription = item.title
                        )
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
