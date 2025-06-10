package dev.tekofx.bibliotecat.model.result

class EmptyResults : SearchResults<EmptyResult>(
    numItems = 0,
    items = emptyList(),
    pages = emptyList()
)