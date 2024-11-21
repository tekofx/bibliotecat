package dev.tekofx.biblioteques.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Icon
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.tekofx.biblioteques.ui.IconResource
import dev.tekofx.biblioteques.ui.theme.Typography

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
    val pagerState = rememberPagerState {
        tabEntries.size
    }
    var isTabClick by remember { mutableStateOf(false) }

    // Set content when clicking tab
    LaunchedEffect(selectedTabIndex) {
        if (isTabClick) {
            pagerState.animateScrollToPage(selectedTabIndex)
            isTabClick = false
        }
    }

    // Set selected tab when scrolling
    LaunchedEffect(pagerState.currentPage) {
        if (!isTabClick) {
            selectedTabIndex = pagerState.currentPage
        }
    }

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
                    onClick = {
                        selectedTabIndex = index
                        isTabClick = true
                    },
                    text = {
                        Text(
                            text = tabEntry.name,
                            style = Typography.bodyLarge,
                            textAlign = TextAlign.Center
                        )
                    },
                    icon = {
                        Icon(
                            painter = tabEntry.icon.asPainterResource(),
                            contentDescription = tabEntry.name
                        )
                    }

                )

            }
        }

        Spacer(modifier = Modifier.height(20.dp))
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) { index ->
            contentScreens.getOrNull(index)?.invoke()

        }

    }
}