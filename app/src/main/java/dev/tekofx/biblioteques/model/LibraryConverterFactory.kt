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
        type: Type, annotations: Array<Annotation>, retrofit: Retrofit
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


                println("\n")


                val timetableDeProva = getTimetable(libraryElement, "estiu")

                val library = Library(
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


    fun getTimetable(jsonObject: JSONObject, estacio: String): Timetable {

        val iniciHorari = getIniciHorari(jsonObject, estacio)
        val timeIntervalsDilluns = getTimeIntervals(jsonObject, estacio, "dilluns")
        val timeIntervalsDimarts = getTimeIntervals(jsonObject, estacio, "dimarts")
        val timeIntervalsDimecres = getTimeIntervals(jsonObject, estacio, "dimecres")
        val timeIntervalsDijous = getTimeIntervals(jsonObject, estacio, "dijous")
        val timeIntervalsDivendres = getTimeIntervals(jsonObject, estacio, "divendres")
        val timeIntervalsDissabte = getTimeIntervals(jsonObject, estacio, "dissabte")
        val timeIntervalsDijumenge = getTimeIntervals(jsonObject, estacio, "diumenge")

        val timetableDeProva = Timetable(
            comenca = iniciHorari,
            dilluns = timeIntervalsDilluns,
            dimarts = timeIntervalsDimarts,
            dimecres = timeIntervalsDimecres,
            dijous = timeIntervalsDijous,
            divendres = timeIntervalsDivendres,
            dissabte = timeIntervalsDissabte,
            diumenge = timeIntervalsDijumenge
        )

        return timetableDeProva

    }

    fun getTimeIntervals(jsonObject: JSONObject, estacio: String, day: String): List<TimeInterval> {


        var timeintervalString =
            jsonObject.getString(String.format("horari_%s_%s", estacio, day)).lowercase()

        println(jsonObject.getString("adreca_nom"))
        println(jsonObject.getString("descripcio"))

        println("string " + timeintervalString)

        val regexMonths =
            "(gener|febrer|març|abril|maig|juny|juliol|agost|setembre|octubre|novembre|desembre)".toRegex()
        val regexDayOfWeek =
            "(dilluns|dimarts|dimecres|dijous|divendres|dissabte|diumenge)".toRegex()

        // Timetable contains text
        if (regexMonths.containsMatchIn(timeintervalString) || regexDayOfWeek.containsMatchIn(
                timeintervalString
            )
        ) {
            println("El string contiene un mes en catalán")
            val timeInterval = TimeInterval(null, null, timeintervalString)
            val timeIntervalsList = mutableListOf<TimeInterval>()
            timeIntervalsList.add(timeInterval)
            return timeIntervalsList
        }

        // If its closed
        if (timeintervalString.contains("tancat")) {
            val timeInterval = TimeInterval(null, null, null)
            val timeIntervalsList = mutableListOf<TimeInterval>()
            timeIntervalsList.add(timeInterval)
            return timeIntervalsList

        }

        // If the string is blank
        if (timeintervalString.isEmpty()) {
            val timeInterval = TimeInterval(null, null, null)
            val timeIntervalsList = mutableListOf<TimeInterval>()
            timeIntervalsList.add(timeInterval)
            return timeIntervalsList
        }


        // If text contains matí o tarda remove it
        if (timeintervalString.contains("matí") || timeintervalString.contains(" tarda")) timeintervalString.replace(
            "matí",
            ""
        ).replace("tarda", "")


        // Format timeintervals
        val regexTime = """(\d+)([:.,])?(\d+)?""".toRegex()
        val timeIntervalsStrings = regexTime.findAll(timeintervalString).map { it.value }.chunked(2)


        val timeIntervals = mutableListOf<TimeInterval>()
        for (timeIntervalString in timeIntervalsStrings) {
            println(timeIntervalString)
            var startTimeString = timeIntervalString[0] // "15:30"
            var endTimeString = timeIntervalString[1]   // "19:30"

            // Fix minutes if it has an extra 0 like in 15:300
            if (endTimeString.length > 5)
                endTimeString = endTimeString.substring(0, 5)

            //Fix minutes if it has an extra 0 like in 15:300
            if (startTimeString.length > 5) {
                startTimeString = startTimeString.substring(0, 5)
            }

            // Fix hours like 13,20 and 14,
            if (startTimeString.contains(",") && startTimeString.length > 3)
                startTimeString = startTimeString.replace(",", ":")
            else
                startTimeString = startTimeString.replace(",", "")

            if (endTimeString.contains(",") && endTimeString.length > 3)
                endTimeString = endTimeString.replace(",", ":")
            else
                endTimeString = endTimeString.replace(",", "")

            val startTime = parseTime(startTimeString) // LocalTime 15:30
            val endTime = parseTime(endTimeString)   // LocalTime 19:30

            val timeInterval = TimeInterval(
                startTime, endTime, null
            )
            timeIntervals.add(timeInterval)

        }

        return timeIntervals
    }


    private fun parseTime(timeString: String): LocalTime? {
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