package dev.tekofx.bibliotecat.model.book

class BookDetails(
    val edition: String?,
    val description: String?,
    val synopsis: String?,
    val isbn: String?,
    val collections: List<String>,
    val topics: List<String>,
    val bookCopiesUrl: String?,
)