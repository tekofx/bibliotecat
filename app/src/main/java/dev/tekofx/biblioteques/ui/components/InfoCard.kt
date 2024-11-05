package dev.tekofx.biblioteques.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.tekofx.biblioteques.ui.theme.Typography

@Composable
fun InfoCard(key: String, value: String) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        tonalElevation = 20.dp,
        shape = RoundedCornerShape(10.dp)
    ) {
        Column (
            modifier = Modifier.padding(10.dp)
        ) {
            Text(text = key, style = Typography.titleMedium)
            Text(text = value)
        }
    }
}

@Preview
@Composable
fun InfoCardPreview() {
    InfoCard("ISBN", "12345678")
}