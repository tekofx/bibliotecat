package dev.tekofx.biblioteques.ui.screens.book

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import dev.tekofx.biblioteques.model.BookResult
import dev.tekofx.biblioteques.model.BookResults
import dev.tekofx.biblioteques.model.EmptyResults
import dev.tekofx.biblioteques.model.GeneralResults
import dev.tekofx.biblioteques.model.SearchResult
import dev.tekofx.biblioteques.navigation.NavigateDestinations
import dev.tekofx.biblioteques.ui.components.PaginatedList
import dev.tekofx.biblioteques.ui.theme.Typography
import dev.tekofx.biblioteques.ui.viewModels.BookViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
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
    val isLoading by bookViewModel.isLoadingResults.observeAsState(false)


    LaunchedEffect(key1 = 1) {
        Log.d("BookResultsScreen", "LaunchedEffect. Query: $query searchType: $searchType")
        bookViewModel.setCanNavigateToResults(false)
        bookViewModel.removeCurrentBook()
    }
    Scaffold(
        modifier = Modifier
            .padding(horizontal = 10.dp)
            .padding(top = 10.dp),
        floatingActionButton = {
            ExtendedFloatingActionButton(
                text = { Text("Cercar") },
                icon = { Icon(Icons.Filled.Search, contentDescription = "") },
                onClick = {
                    navHostController.navigate(NavigateDestinations.BOOK_SEARCH_ROUTE)
                }
            )
        }
    ) {


        when (results) {
            is BookResults ->
                PaginatedList(
                    searchResults = results as BookResults,
                    isLoading = isLoading,
                    key = { book -> book.id },
                    onLoadMore = { bookViewModel.getNextResultsPage() },

                    ) { book ->
                    BookCard(
                        book = book as BookResult,
                        onClick = {
                            navHostController.navigate("${NavigateDestinations.BOOK_DETAILS_ROUTE}/${book.id}")
                        }
                    )
                }

            is GeneralResults ->
                PaginatedList(
                    searchResults = results as GeneralResults,
                    isLoading = isLoading,
                    key = { searchResult: SearchResult -> searchResult.text },
                    onLoadMore = { bookViewModel.getNextResultsPage() },
                ) { searchResult ->
                    GeneralSearchResultCard(
                        onClick = { bookViewModel.getResults(searchResult.url) },
                        searchResult = searchResult
                    )
                }
        }

    }
}

@Composable
fun BookCard(
    book: BookResult,
    onClick: () -> Unit
) {

    Surface(
        tonalElevation = 40.dp,
        shape = RoundedCornerShape(20.dp),
        onClick = { onClick() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(10.dp)
        ) {

            Row(
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                AsyncImage(
                    model = book.image,
                    contentDescription = null,
                    modifier = Modifier
                        .height(200.dp)
                        .aspectRatio(0.6f)
                        .clip(RoundedCornerShape(10.dp)),
                    contentScale = ContentScale.Crop
                )
                Column(
                    modifier = Modifier.fillMaxHeight(),
                    verticalArrangement = Arrangement.Top
                ) {
                    Text(
                        text = book.title,
                        style = Typography.titleLarge
                    )
                    Text(
                        text = book.author,
                        style = Typography.titleMedium
                    )
                    book.publication?.let {
                        Text(
                            text = it,
                            style = Typography.titleMedium
                        )
                    }
                }
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
        shape = RoundedCornerShape(10.dp),
        onClick = { onClick() }
    )
    {
        Row(
            modifier = Modifier
                .padding(10.dp)
                .padding(vertical = 20.dp),
        ) {
            Text(
                modifier = Modifier.weight(2f),
                text = searchResult.text
            )
            Text(
                text = searchResult.numEntries.toString()
            )
        }
    }
}