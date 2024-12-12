package dev.tekofx.biblioteques.dto

import com.google.gson.annotations.SerializedName
import dev.tekofx.biblioteques.model.HolidayDay


data class HolidayResponse(
    @SerializedName("body") var body: List<HolidayDay> = emptyList(),
)
