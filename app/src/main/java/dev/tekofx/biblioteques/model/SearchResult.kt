package dev.tekofx.biblioteques.model

class SearchResult(
    val text: String,
    val url: String,
    val entries: Int,
) {
    override fun toString(): String {
        return text
    }
}