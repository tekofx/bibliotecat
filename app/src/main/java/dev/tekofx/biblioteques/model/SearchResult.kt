package dev.tekofx.biblioteques.model


abstract class SearchResults<SearchResult>(
    open var items: List<SearchResult>,
    open val pages: List<String>,
    open val numItems: Int
) {
    var currentPage: Int = 0

    fun areMorePages(): Boolean {
        return pages.isNotEmpty()
    }

    fun getNextPage(): String? {
        if (pages.isEmpty()) {
            return null
        }
        val page = pages[currentPage]
        currentPage++
        return page
    }

    fun addItems(newItems: List<SearchResult>) {
        items = items.plus(newItems)

    }


}

class EmptyResult : SearchResult(
    id = 0,
    numEntries = null,
    text = "",
    url = ""
)

class EmptyResults : SearchResults<EmptyResult>(
    numItems = 0,
    items = emptyList(),
    pages = emptyList()
)

class GeneralResults(
    override var items: List<GeneralResult>,
    override val pages: List<String>,
    override val numItems: Int
) : SearchResults<GeneralResult>(
    items, pages, numItems
)

class BookResults(
    override var items: List<BookResult>,
    override val pages: List<String>,
    override val numItems: Int
) : SearchResults<BookResult>(
    items, pages, numItems
)

abstract class SearchResult(
    open val id: Int,
    open val text: String,
    open val url: String,
    open val numEntries: Int?
)

class BookResult(
    override val id: Int,
    val title: String,
    val author: String,
    val publication: String?,
    override val url: String,
    val image: String?,
) : SearchResult(
    id, title, url, null
)


class GeneralResult(
    override val id: Int,
    override val text: String,
    override val url: String,
    override val numEntries: Int
) : SearchResult(
    id, text, url, numEntries,
)

