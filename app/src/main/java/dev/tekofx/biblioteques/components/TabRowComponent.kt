package dev.tekofx.biblioteques.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
    containerColor: Color = Color.Gray,
    contentColor: Color = Color.White,
    indicatorColor: Color = Color.DarkGray
) {

    // State to keep track of the selected tab index
    var selectedTabIndex by remember { mutableIntStateOf(0) }

    // Column layout to arrange tabs vertically and display content screens
    Column(modifier = modifier.fillMaxSize()) {
        // TabRow composable to display tabs
        TabRow(
            selectedTabIndex = selectedTabIndex,
            containerColor = containerColor,
            contentColor = contentColor,
            indicator = { tabPositions ->
                // Indicator for the selected tab
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                    color = indicatorColor
                )
            }
        ) {
            // Iterate through each tab title and create a tab
            tabEntries.forEachIndexed { index, tabEntry ->
                Tab(
                    modifier = Modifier.padding(all = 16.dp),
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index }
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Icon(
                            tabEntry.icon.asPainterResource(),
                            contentDescription = tabEntry.name
                        )
                        Text(text = tabEntry.name)
                    }
                }
            }
        }

        // Display the content screen corresponding to the selected tab
        contentScreens.getOrNull(selectedTabIndex)?.invoke()
    }
}