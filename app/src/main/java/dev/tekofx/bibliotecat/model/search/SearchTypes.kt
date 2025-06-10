package dev.tekofx.bibliotecat.model.search

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import dev.tekofx.bibliotecat.R
import dev.tekofx.bibliotecat.ui.IconResource


val SearchTypeAnyWord= SearchArgument("Qualsevol paraula", "X", IconResource.fromDrawableResource(R.drawable.abc))
val SearchTypeTitle= SearchArgument("Títol", "t", IconResource.fromDrawableResource(R.drawable.title))
val SearchTypeAuthor= SearchArgument("Autor/Artista", "a", IconResource.fromImageVector(Icons.Filled.Person))
val SearchTypeTopic= SearchArgument("Tema", "d", IconResource.fromDrawableResource(R.drawable.topic))
val SearchTypeISBN= SearchArgument("ISBN/ISSN", "i", IconResource.fromDrawableResource(R.drawable.numbers))
val SearchTypeMagazineLocation= SearchArgument("Lloc de publicació de revistas", "m", IconResource.fromDrawableResource(R.drawable.location_city))
val SearchTypeSignature= SearchArgument("Signatura", "c", IconResource.fromDrawableResource(R.drawable.assignment))

val SearchTypes = listOf(
    SearchTypeAnyWord,
    SearchTypeTitle,
    SearchTypeAuthor,
    SearchTypeTopic,
    SearchTypeISBN,
    SearchTypeMagazineLocation,
    SearchTypeSignature
)