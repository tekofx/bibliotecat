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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import dev.tekofx.biblioteques.ui.components.book.BooksList
import dev.tekofx.biblioteques.ui.viewModels.BookViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun BooksScreen(
    navHostController: NavHostController,
    bookViewModel: BookViewModel
) {

    val books by bookViewModel.books.observeAsState(emptyList())

    val isLoading by bookViewModel.isLoading.observeAsState(false)
    val errorMessage by bookViewModel.errorMessage.observeAsState()

    val density = LocalDensity.current
    val focus = LocalFocusManager.current

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


        BooksList(books, navHostController, bookViewModel)
        AnimatedVisibility(
            visible = books.isEmpty(),
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
                    value = bookViewModel.queryText,
                    onValueChange = { newText ->
                        bookViewModel.onSearchTextChanged(newText)
                    },
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
                    label = { Text("Cerca qualsevol paraula") }
                )
                Button(
                    onClick = {
                        bookViewModel.findBooks()
                        focus.clearFocus()
                    },
                    enabled = bookViewModel.queryText.isNotEmpty() && !isLoading

                ) {

                    Text(text = "Cerca")
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
}

