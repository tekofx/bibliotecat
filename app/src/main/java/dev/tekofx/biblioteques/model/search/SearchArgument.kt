package dev.tekofx.biblioteques.model.search

import dev.tekofx.biblioteques.ui.IconResource

class SearchArgument(
    val name: String,
    val value: String,
    val icon: IconResource
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SearchArgument

        if (name != other.name) return false
        if (value != other.value) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + value.hashCode()
        return result
    }

    override fun toString(): String {
        return name
    }

}