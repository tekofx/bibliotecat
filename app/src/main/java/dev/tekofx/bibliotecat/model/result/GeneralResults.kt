package dev.tekofx.bibliotecat.model.result

class GeneralResults(
    override var items: List<GeneralResult>,
    override val pages: List<String>,
    override val numItems: Int
) : SearchResults<GeneralResult>(
    items, pages, numItems
)