package dev.tekofx.biblioteques.model.result

class EmptyResults : SearchResults<EmptyResult>(
    numItems = 0,
    items = emptyList(),
    pages = emptyList()
)