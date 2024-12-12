package dev.tekofx.biblioteques.repository

import android.util.Log
import dev.tekofx.biblioteques.call.HolidayService
import dev.tekofx.biblioteques.dto.HolidayResponse
import retrofit2.Call

class HolidayRepository(private val holidayService: HolidayService) {
    fun getJson(query: String): Call<HolidayResponse> {
        Log.d("BookRepository", "Request JSON")
        return holidayService.getJson(query)
    }


}
