package dev.tekofx.biblioteques.model

import dev.tekofx.biblioteques.ui.IconResource

class SelectItem(
    val text: String,
    val value: String,
    val icon: IconResource
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SelectItem

        if (text != other.text) return false
        if (value != other.value) return false

        return true
    }

    override fun hashCode(): Int {
        var result = text.hashCode()
        result = 31 * result + value.hashCode()
        return result
    }

}