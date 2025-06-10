package dev.tekofx.bibliotecat.ui.screens

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.tekofx.bibliotecat.ui.components.Map
import dev.tekofx.bibliotecat.ui.viewModels.library.LibraryViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MapScreen(
    pointID: String?,
    libraryViewModel: LibraryViewModel,
) {
    Log.d("LibraryScreen", "Navigated to $pointID")

    // Data
    val currentLibrary by libraryViewModel.currentLibrary.collectAsState()

    LaunchedEffect(key1 = Unit) {
        Log.d("MapScreen", "pointId $pointID")
        libraryViewModel.getLibrary(pointID, null)
    }

    Scaffold(
        topBar = {
            Surface(
                tonalElevation = 20.dp,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(10.dp)
                ) {

                    currentLibrary?.let { Text(it.name) }
                }
            }
        }
    ) {
        currentLibrary?.let { Map(library = it, onClick = {}) }
    }


}