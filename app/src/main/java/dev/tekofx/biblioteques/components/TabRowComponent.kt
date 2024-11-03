package dev.tekofx.biblioteques.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Icon
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.tekofx.biblioteques.ui.IconResource

class TabEntry(
    val name: String,
    val icon: IconResource,
)

@Composable
fun TabRowComponent(
    tabEntries: List<TabEntry>,
    contentScreens: List<@Composable () -> Unit>,
    modifier: Modifier = Modifier,
) {

    // State to keep track of the selected tab index
    var selectedTabIndex by remember { mutableIntStateOf(0) }

    // Column layout to arrange tabs vertically and display content screens
    Column(modifier = modifier.fillMaxSize()) {
        // TabRow composable to display tabs
        TabRow(
            selectedTabIndex = selectedTabIndex,

            ) {
            // Iterate through each tab title and create a tab
            tabEntries.forEachIndexed { index, tabEntry ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = { Text(text = tabEntry.name) },
                    icon = {
                        Icon(
                            painter = tabEntry.icon.asPainterResource(),
                            contentDescription = tabEntry.name
                        )
                    }

                )

            }
        }

        // Display the content screen corresponding to the selected tab
        Spacer(modifier = Modifier.height(10.dp))
        contentScreens.getOrNull(selectedTabIndex)?.invoke()
    }
}