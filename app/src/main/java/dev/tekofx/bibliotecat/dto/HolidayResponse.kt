package dev.tekofx.bibliotecat.dto

import com.google.gson.annotations.SerializedName
import dev.tekofx.bibliotecat.model.holiday.Holiday
import java.time.LocalDate


data class HolidayResponse(
    @SerializedName("body") var body: Map<LocalDate, Holiday> = emptyMap<LocalDate, Holiday>(),
)
