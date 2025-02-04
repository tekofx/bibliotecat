package dev.tekofx.biblioteques.model.library

enum class Season {
    WINTER,
    SUMMER
}

val seasonTranslation = mapOf(
    Season.WINTER to "Hivern",
    Season.SUMMER to "Estiu"
)