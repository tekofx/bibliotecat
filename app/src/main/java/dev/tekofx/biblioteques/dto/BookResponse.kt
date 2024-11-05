package dev.tekofx.biblioteques.dto

import com.google.gson.annotations.SerializedName

data class BookResponse(
    @SerializedName("body") var body: String = ""
)