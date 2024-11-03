package dev.tekofx.biblioteques.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun BooksScreen() {
    Column(Modifier.fillMaxSize()) {
        Text(
            text = "DashboardScreen",
            style = MaterialTheme.typography.headlineMedium
        )
        Text(
            text = "test2",
            style = MaterialTheme.typography.headlineSmall
        )
        Text(
            text = "test3",
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.weight(1f))
        Button(onClick = { /* Handle click */ }, Modifier.fillMaxWidth()) {
            Text(text = "Button")
        }
    }
}