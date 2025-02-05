package dev.tekofx.biblioteques.model.result

class GeneralResult(
    override val id: Int,
    override val text: String,
    override val url: String,
    override val numEntries: Int
) : SearchResult(
    id, text, url, numEntries,
)