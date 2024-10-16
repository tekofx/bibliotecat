package dev.tekofx.biblioteques.ui.home

import com.google.gson.annotations.SerializedName

data class BibliotecasResponse (
    @SerializedName("elements") var elements: ArrayList<Biblioteca> = arrayListOf()
)