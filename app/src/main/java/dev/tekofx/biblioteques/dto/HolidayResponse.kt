package dev.tekofx.biblioteques.dto

import com.google.gson.annotations.SerializedName
import dev.tekofx.biblioteques.model.holiday.Holiday


data class HolidayResponse(
    @SerializedName("body") var body: List<Holiday> = emptyList(),
)
