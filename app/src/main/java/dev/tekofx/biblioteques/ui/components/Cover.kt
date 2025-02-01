package dev.tekofx.biblioteques.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import dev.tekofx.biblioteques.model.search.BookResult
import dev.tekofx.biblioteques.model.book.Book
import kotlin.random.Random

@Composable
fun Cover(book: Book? = null, bookResult: BookResult? = null) {
    val randomColor = Color(
        red = Random.nextFloat(),
        green = Random.nextFloat(),
        blue = Random.nextFloat(),
        alpha = 1f
    )
    Surface(
        modifier = Modifier
            .height(200.dp)
            .aspectRatio(0.6f)
            .clip(RoundedCornerShape(10.dp)),
        color=randomColor
    ) {
        Column {
            book?.let{
                Text(book.title)
                Text(book.author)
            }

            bookResult?.let{
                Text(bookResult.title)
                Text(bookResult.author)
            }
        }
    }
}