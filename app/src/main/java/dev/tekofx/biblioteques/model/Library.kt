package dev.tekofx.biblioteques.model

import com.google.gson.annotations.SerializedName

data class Library(
    @SerializedName("punt_id")
    var puntId: String,

    @SerializedName("adreca_nom")
    var adrecaNom: String,

    @SerializedName("descripcio")
    var descripcio: String,

    @SerializedName("municipi_nom")
    var municipiNom: String,

    @SerializedName("imatge")
    var imatge: Array<String>,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Library

        if (puntId != other.puntId) return false
        if (adrecaNom != other.adrecaNom) return false
        if (descripcio != other.descripcio) return false
        if (municipiNom != other.municipiNom) return false
        if (!imatge.contentEquals(other.imatge)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = puntId.hashCode()
        result = 31 * result + adrecaNom.hashCode()
        result = 31 * result + descripcio.hashCode()
        result = 31 * result + municipiNom.hashCode()
        result = 31 * result + imatge.contentHashCode()
        return result
    }
}

