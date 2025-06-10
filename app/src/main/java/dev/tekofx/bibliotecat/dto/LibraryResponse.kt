package dev.tekofx.bibliotecat.dto

import com.google.gson.annotations.SerializedName
import dev.tekofx.bibliotecat.model.library.Library

data class LibraryResponse(
    @SerializedName("elements") var elements: List<Library> = arrayListOf(),
    @SerializedName("municipalities") var municipalities: List<String> = arrayListOf()
)