package dev.tekofx.bibliotecat.model.result

abstract class SearchResult(
    open val id: Int,
    open val text: String,
    open val url: String,
    open val numEntries: Int?
)
