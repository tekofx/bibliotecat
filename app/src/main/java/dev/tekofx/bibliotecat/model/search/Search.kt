package dev.tekofx.bibliotecat.model.search

import dev.tekofx.bibliotecat.R
import dev.tekofx.bibliotecat.ui.IconResource


data class Search(

    /**
     * The query to search
     */
    val query: String = "",

    /**
     * The type of search, like title, author, etc
     */
    val searchType: SearchArgument = SearchTypes.first(),

    /**
     * The scope of the search, like all catalog, movies, Barcelona, etc
     */
    val catalog: SearchArgument = SearchArgument(
        "Tot el catàleg",
        "171",
        icon = IconResource.fromDrawableResource(R.drawable.library_books)
    )
)
