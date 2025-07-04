package dev.tekofx.bibliotecat.model.holiday

import dev.tekofx.bibliotecat.dto.HolidayResponse
import okhttp3.ResponseBody
import org.json.JSONArray
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.reflect.Type
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class HolidayConverterFactory : Converter.Factory() {
    private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS")

    override fun responseBodyConverter(
        type: Type, annotations: Array<Annotation>, retrofit: Retrofit
    ): Converter<ResponseBody, HolidayResponse> {
        return Converter { responseBody ->
            responseBody.use { body ->

                val data = body.string()
                val jsonArray = JSONArray(data)

                val holidayList: Map<LocalDate, Holiday> =
                    if (jsonArray.getJSONObject(0).has("any_calendari")) {
                        constructLocalHolidays(jsonArray)
                    } else {
                        constructGeneralCataloniaHolidays(jsonArray)
                    }

                HolidayResponse(holidayList)
            }
        }
    }

    private fun constructGeneralCataloniaHolidays(jsonArray: JSONArray): Map<LocalDate, Holiday> {
        val localHolidayList = mutableMapOf<LocalDate, Holiday>()
        for (i in 0 until jsonArray.length()) {
            val jsonObject = jsonArray.getJSONObject(i)
            val year = jsonObject.getInt("any")
            val localDateTime = LocalDateTime.parse(jsonObject.getString("data"), formatter)
            val localDate = localDateTime.toLocalDate()
            val holidayName = jsonObject.getString("nom_del_festiu")

            val holiday = Holiday(
                year = year,
                date = localDate,
                place = "Catalunya",
                name = holidayName,
                postalCode = "08"
            )
            localHolidayList[localDate] = holiday
        }
        return localHolidayList
    }

    private fun constructLocalHolidays(jsonArray: JSONArray): Map<LocalDate, Holiday> {
        val localHolidayList = mutableMapOf<LocalDate, Holiday>()
        for (i in 0 until jsonArray.length()) {
            val jsonObject = jsonArray.getJSONObject(i)
            val year = jsonObject.getInt("any_calendari")
            val localDateTime = LocalDateTime.parse(jsonObject.getString("data"), formatter)
            val localDate = localDateTime.toLocalDate()
            val place = jsonObject.getString("ajuntament_o_nucli_municipal")
            val postalCode = jsonObject.getString("codi_municipi_ine")
            val holidayObject = jsonObject.getString("festiu")

            val holiday = Holiday(
                year = year,
                date = localDate,
                place = place,
                name = holidayObject,
                postalCode = postalCode
            )
            localHolidayList[localDate] = holiday
        }
        return localHolidayList
    }


    companion object {
        fun create(): HolidayConverterFactory {
            return HolidayConverterFactory()
        }
    }
}