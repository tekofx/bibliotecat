package dev.tekofx.biblioteques.ui.screens.book

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import dev.tekofx.biblioteques.model.BookResult
import dev.tekofx.biblioteques.model.BookResults
import dev.tekofx.biblioteques.model.EmptyResults
import dev.tekofx.biblioteques.model.GeneralResult
import dev.tekofx.biblioteques.model.GeneralResults
import dev.tekofx.biblioteques.model.SearchResult
import dev.tekofx.biblioteques.navigation.NavigateDestinations
import dev.tekofx.biblioteques.ui.components.PaginatedList
import dev.tekofx.biblioteques.ui.components.book.BookCard
import dev.tekofx.biblioteques.ui.viewModels.BookViewModel

@Composable
fun BookResultsScreen(
    navHostController: NavHostController,
    bookViewModel: BookViewModel,
    query: String?,
    searchType: String?
) {

    val results by bookViewModel.results.observeAsState(
        EmptyResults()
    )
    val isLoading by bookViewModel.isLoading.observeAsState(false)


    LaunchedEffect(key1 = 1) {
        Log.d("BookResultsScreen", "LaunchedEffect. Query: $query searchType: $searchType")
        bookViewModel.setOnResultsScreen(true)
    }

    Column(
        modifier = Modifier.padding(10.dp)
    ) {

        when (results) {
            is BookResults ->
                PaginatedList<BookResult>(
                    items = results.items,
                    isLoading = isLoading,
                    key = { book -> book.id },
                    onLoadMore = { bookViewModel.getNextResultsPage() }

                ) { book ->
                    BookCard(
                        book = book as BookResult,
                        onClick = {
                            navHostController.navigate("${NavigateDestinations.BOOKS_ROUTE}/${book.id}")
                        }
                    )
                }

            is GeneralResults ->
                PaginatedList<GeneralResult>(
                    items = results.items,
                    isLoading = isLoading,
                    key = { searchResult: SearchResult -> searchResult.text },
                    onLoadMore = { bookViewModel.getNextResultsPage() }
                ) { searchResult ->
                    GeneralSearchResultCard(
                        onClick = { bookViewModel.getBooksBySearchResult(searchResult.url) },
                        searchResult = searchResult
                    )
                }
        }

    }
}

@Composable
fun GeneralSearchResultCard(
    onClick: () -> Unit,
    searchResult: SearchResult
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        tonalElevation = 20.dp,
        onClick = { onClick() }
    )
    {
        Row(
            modifier = Modifier.padding(vertical = 50.dp),
        ) {
            Text(
                text = searchResult.text
            )
            Text(
                text = searchResult.numEntries.toString()
            )
        }
    }
}