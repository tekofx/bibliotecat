package dev.tekofx.biblioteques.model.result

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
