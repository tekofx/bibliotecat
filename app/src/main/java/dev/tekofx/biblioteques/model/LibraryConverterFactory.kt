package dev.tekofx.biblioteques.model

import com.fleeksoft.ksoup.Ksoup
import dev.tekofx.biblioteques.dto.LibraryResponse
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.reflect.Type
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException



class LibraryConverterFactory : Converter.Factory() {

    val timeFormatter = DateTimeFormatter.ofPattern("[H:mm][HH:mm]")


    override fun responseBodyConverter(
        type: Type,
        annotations: Array<Annotation>,
        retrofit: Retrofit
    ): Converter<ResponseBody, LibraryResponse>? {
        return Converter { responseBody ->
            val jsonResponse = responseBody.string()
            val jsonObject = JSONObject(jsonResponse)

            val elementsArray = jsonObject.getJSONArray("elements")
            val libraryList = ArrayList<Library>()

            for (i in 0 until elementsArray.length()) {
                val libraryElement = elementsArray.getJSONObject(i)
                val imatgeArray = libraryElement.getJSONArray("imatge")
                val puntId = libraryElement.getString("punt_id")
                val adrecaNom = libraryElement.getString("adreca_nom")
                val descripcio = libraryElement.getString("descripcio")
                val municipiNom =
                    libraryElement.getJSONObject("grup_adreca").getString("municipi_nom")
                val imatge = if (imatgeArray.length() > 0) imatgeArray.getString(0) else ""


                // Horaris




                val timetableDeProva = Timetable(
                    comenca = LocalDate.of(2024, 10, 17),
                    dilluns = listOf(
                        TimeInterval(LocalTime.of(9, 0), LocalTime.of(10, 0)),
                        TimeInterval(LocalTime.of(11, 0), LocalTime.of(12, 0))
                    ),
                    dimarts = listOf(
                        TimeInterval(LocalTime.of(9, 0), LocalTime.of(10, 0)),
                        TimeInterval(LocalTime.of(11, 0), LocalTime.of(12, 0))
                    ),
                    dimecres = listOf(
                        TimeInterval(LocalTime.of(10, 0), LocalTime.of(11, 0)),
                        TimeInterval(LocalTime.of(12, 0), LocalTime.of(13, 0))
                    ),
                    dijous = listOf(
                        TimeInterval(LocalTime.of(8, 0), LocalTime.of(9, 0)),
                        TimeInterval(LocalTime.of(10, 0), LocalTime.of(11, 0))
                    ),
                    divendres = listOf(
                        TimeInterval(LocalTime.of(9, 0), LocalTime.of(10, 0)),
                        TimeInterval(LocalTime.of(11, 0), LocalTime.of(12, 0))
                    ),
                    dissabte = listOf(
                        TimeInterval(LocalTime.of(10, 0), LocalTime.of(11, 0)),
                        TimeInterval(LocalTime.of(12, 0), LocalTime.of(13, 0))
                    ),
                    diumenge = listOf(
                        TimeInterval(LocalTime.of(11, 0), LocalTime.of(12, 0)),
                        TimeInterval(LocalTime.of(13, 0), LocalTime.of(14, 0))
                    )
                )

                val library =
                    Library(
                        puntId,
                        adrecaNom,
                        descripcio,
                        municipiNom,
                        imatge,
                        timetableDeProva,
                        timetableDeProva,
                        timetableDeProva
                    )
                // Rellena los demás atributos según sea necesario
                libraryList.add(library)
            }

            val response = LibraryResponse(libraryList)
            response
        }
    }








    fun parseTime(timeString: String): LocalTime? {
        return try {
            var timeString = timeString
            if (timeString.length < 3) {
                timeString += ":00"
            }

            LocalTime.parse(timeString.replace(".", ":"), timeFormatter)
        } catch (e: DateTimeParseException) {
            println("Error parsing time: $timeString")
            throw RuntimeException("Time parsing failed")
            null
        }
    }



    fun parseMonth(monthName: String): Int {
        return when {
            monthName.contains("gener", ignoreCase = true) -> 1
            monthName.contains("febrer", ignoreCase = true) -> 2
            monthName.contains("març", ignoreCase = true) -> 3
            monthName.contains("abril", ignoreCase = true) -> 4
            monthName.contains("maig", ignoreCase = true) -> 5
            monthName.contains("juny", ignoreCase = true) -> 6
            monthName.contains("juliol", ignoreCase = true) -> 7
            monthName.contains("agost", ignoreCase = true) -> 8
            monthName.contains("setembre", ignoreCase = true) -> 9
            monthName.contains("octubre", ignoreCase = true) -> 10
            monthName.contains("novembre", ignoreCase = true) -> 11
            monthName.contains("desembre", ignoreCase = true) -> 12
            else -> {
                return 0
            }


        }
    }

    private fun getIniciHorari(jsonObject: JSONObject, estacio: String): LocalDate? {

        var htmlString = jsonObject.getString("inici_horari_$estacio")

        val doc = Ksoup.parse(htmlString)
        var span = doc.selectFirst("span")
        var day = 0;
        var month = 0;



        if (span != null && span.text().isNotEmpty()) {
            val dateParts = span.text().split(" ")
            day = dateParts[0].toInt()
            month = parseMonth(span.text())

        } else {
            return null
        }
        // TODO: Change Year
        return LocalDate.of(2024, month, day)


    }

    companion object {
        fun create(): LibraryConverterFactory {
            return LibraryConverterFactory()
        }
    }
}