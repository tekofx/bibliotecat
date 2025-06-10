package dev.tekofx.bibliotecat.model.result

class BookResults(
    override var items: List<BookResult>,
    override val pages: List<String>,
    override val numItems: Int
) : SearchResults<BookResult>(
    items, pages, numItems
)