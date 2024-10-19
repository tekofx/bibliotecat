package dev.tekofx.biblioteques.dto

import com.google.gson.annotations.SerializedName
import dev.tekofx.biblioteques.model.Library

data class LibraryResponse(
    @SerializedName("elements") var elements: ArrayList<Library> = arrayListOf()
)