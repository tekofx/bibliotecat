package dev.tekofx.biblioteques.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
        ) {
            Text(text = key, style = Typography.titleMedium, fontWeight = FontWeight.Bold)
            Text(
                text = value,
                textAlign = TextAlign.Justify,
                modifier = Modifier
                    .fillMaxWidth(),
            )
        }
    }
}

@Preview
@Composable
fun InfoCardISBNPreview() {
    InfoCard("ISBN", "12345678")
}

@Preview
@Composable
fun InfoCardDescriptionPreview() {
    InfoCard(
        "Description",
        "Han transcurrido trescientos años desde los acontecimientos de la Triología Original Mistborn. Kelsier y Vin han pasado a formar parte de la historia y la mitología, y el mundo de Scadrial se halla a las puertas de la modernidad. Sin embargo, en las tierras fronterizas conocidas como los Áridos, las antiguas magias todavía son una herramienta crucial para quienes defienden el orden y la justicia." )
}