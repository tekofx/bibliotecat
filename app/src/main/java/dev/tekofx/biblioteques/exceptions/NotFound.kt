package dev.tekofx.biblioteques.exceptions

class NotFound : Exception() {
    override val message: String
        get() = "Not found"
}