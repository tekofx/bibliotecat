package dev.tekofx.biblioteques.ui.components.book

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.tekofx.biblioteques.model.book.BookCopy
import dev.tekofx.biblioteques.ui.components.StatusBadge
import dev.tekofx.biblioteques.ui.theme.Typography

@Composable
fun BookCopyCard(bookCopy: BookCopy) {
    Surface(
        tonalElevation = 20.dp,
        shape = RoundedCornerShape(10.dp),
    ) {
        Column(
            modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = bookCopy.location,
                style = Typography.titleMedium
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(text = "Signatura")
                Text(text = bookCopy.signature)

            }
            bookCopy.notes?.let {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(text = "Notes")
                    Text(text = it)
                }
            }
            StatusBadge(
                bookCopy.statusColor,
                text = bookCopy.status,
                textStyle = Typography.bodyMedium
            )

        }
    }
}

