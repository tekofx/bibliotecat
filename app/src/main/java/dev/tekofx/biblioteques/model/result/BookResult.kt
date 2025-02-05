package dev.tekofx.biblioteques.model.result

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
