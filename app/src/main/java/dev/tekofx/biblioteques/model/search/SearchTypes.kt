package dev.tekofx.biblioteques.model.search

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import dev.tekofx.biblioteques.R
import dev.tekofx.biblioteques.ui.IconResource

val SearchTypes = listOf(
    SearchArgument("Qualsevol paraula", "X", IconResource.fromDrawableResource(R.drawable.abc)),
    SearchArgument("Títol", "t", IconResource.fromDrawableResource(R.drawable.title)),
    SearchArgument("Autor/Artista", "a", IconResource.fromImageVector(Icons.Filled.Person)),
    SearchArgument("Tema", "d", IconResource.fromDrawableResource(R.drawable.topic)),
    SearchArgument("ISBN/ISSN", "i", IconResource.fromDrawableResource(R.drawable.numbers)),
    SearchArgument(
        "Lloc de publicació de revistas",
        "m",
        IconResource.fromDrawableResource(R.drawable.location_city)
    ),
    SearchArgument("Signatura", "c", IconResource.fromDrawableResource(R.drawable.assignment)),
)