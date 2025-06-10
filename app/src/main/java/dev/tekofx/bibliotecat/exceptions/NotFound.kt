package dev.tekofx.bibliotecat.exceptions

class NotFound : Exception() {
    override val message: String
        get() = "Not found"
}