package dev.tekofx.biblioteques.ui.screens.book

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import dev.tekofx.biblioteques.model.SearchResult
import dev.tekofx.biblioteques.model.book.Book
import dev.tekofx.biblioteques.ui.IconResource
import dev.tekofx.biblioteques.ui.components.ButtonSelect
import dev.tekofx.biblioteques.ui.components.ButtonSelectItem
import dev.tekofx.biblioteques.ui.components.PaginatedList
import dev.tekofx.biblioteques.ui.components.TextIconButton
import dev.tekofx.biblioteques.ui.components.book.BookCard
import dev.tekofx.biblioteques.ui.viewModels.BookViewModel
import dev.tekofx.biblioteques.ui.viewModels.searchTypes


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun BooksScreen(
    navHostController: NavHostController,
    bookViewModel: BookViewModel
) {

    val books by bookViewModel.books.observeAsState(emptyList())
    val searchResults by bookViewModel.searchResults.observeAsState(emptyList())

    val isLoading by bookViewModel.isLoading.observeAsState(false)
    val errorMessage by bookViewModel.errorMessage.observeAsState()

    var selectedSearchTpe by bookViewModel.selectedSearchType

    Scaffold(
        floatingActionButton = {
            if (books.isNotEmpty()) {
                ExtendedFloatingActionButton(
                    text = { Text("Cercar") },
                    icon = { Icon(Icons.Filled.Search, contentDescription = "") },
                    onClick = {
                    }
                )
            }
        }
    ) {

        PaginatedList(
            items = books,
            isLoading = isLoading,
            key = { book -> book.id },
            onLoadMore = { bookViewModel.getNextResultsPage() }

        ) { book -> BookCard(book, navHostController) }

        PaginatedList(
            items = searchResults,
            isLoading = isLoading,
            key = { searchResult: SearchResult -> searchResult.text },
            onLoadMore = {}
        ) { searchResult ->
            Surface(
                onClick = {
                    bookViewModel.getBooksBySearchResult(searchResult.url)
                }
            )
            {
                Row {
                    Text(
                        text = searchResult.text
                    )
                    Text(
                        text = searchResult.entries.toString()
                    )
                }
            }
        }

        BookSearch(
            books = books,
            searchResults = searchResults,
            errorMessage = errorMessage,
            onSearchTextChanged = { bookViewModel.onSearchTextChanged(it) },
            queryText = bookViewModel.queryText,
            isLoading = isLoading,
            selectedSearchTpe = selectedSearchTpe,
            onSearch = { bookViewModel.search() },
            onOptionSelected = { selectedSearchTpe = it }
        )

    }
}

@Composable
fun BookSearch(
    books: List<Book>,
    searchResults: List<SearchResult>,
    errorMessage: String?,
    onSearchTextChanged: (String) -> Unit,
    queryText: String,
    selectedSearchTpe: ButtonSelectItem,
    isLoading: Boolean,
    onOptionSelected: (ButtonSelectItem) -> Unit,
    onSearch: () -> Unit
) {
    val focus = LocalFocusManager.current
    val density = LocalDensity.current

    AnimatedVisibility(
        visible = books.isEmpty() && searchResults.isEmpty(),
        exit = slideOutVertically(targetOffsetY = { it })
                + fadeOut()
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 10.dp)
                .fillMaxHeight(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterVertically)
        ) {
            if (!errorMessage.isNullOrEmpty()) {
                Text(text = "Llibre no trobat :(")
            }
            TextField(
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                ),
                value = queryText,
                onValueChange = { onSearchTextChanged(it) },
                singleLine = true,
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.Search,
                        contentDescription = null
                    )
                },
                shape = RoundedCornerShape(50.dp),
                modifier = Modifier
                    .fillMaxWidth(),
                label = { Text("Cerca ${selectedSearchTpe.text.lowercase()}") }
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ButtonSelect(
                    selectedOption = selectedSearchTpe,
                    options = searchTypes,
                    onOptionSelected = {
                        onOptionSelected(it)
                    }
                )
                TextIconButton(
                    text = "Cerca",
                    icon = IconResource.fromImageVector(Icons.Outlined.Search),
                    enabled = queryText.isNotEmpty() && !isLoading,
                    onClick = {
                        onSearch()
                        focus.clearFocus()
                    }
                )
            }
            AnimatedVisibility(
                visible = isLoading,
                enter = slideInVertically {
                    // Slide in from 40 dp from the top.
                    with(density) { -40.dp.roundToPx() }
                } + expandVertically(
                    // Expand from the top.
                    expandFrom = Alignment.Top
                ) + fadeIn(
                    // Fade in with the initial alpha of 0.3f.
                    initialAlpha = 0.3f
                ),
                exit = slideOutVertically() + shrinkVertically(
                    shrinkTowards = Alignment.Bottom
                ) + fadeOut()
            ) {
                CircularProgressIndicator()
            }
        }
    }
}