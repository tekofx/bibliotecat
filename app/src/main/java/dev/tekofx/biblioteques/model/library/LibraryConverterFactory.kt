package dev.tekofx.biblioteques.model.library

import android.util.Log
import com.fleeksoft.ksoup.Ksoup
import com.fleeksoft.ksoup.network.parseGetRequestBlocking
import com.fleeksoft.ksoup.nodes.Document
import dev.tekofx.biblioteques.dto.LibraryResponse
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.reflect.Type
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException


class LibraryConverterFactory : Converter.Factory() {

    private val timeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("[H:mm][HH:mm]")

    override fun responseBodyConverter(
        type: Type, annotations: Array<Annotation>, retrofit: Retrofit
    ): Converter<ResponseBody, LibraryResponse> {
        return Converter { responseBody ->
            val jsonResponse = responseBody.string()
            val jsonObject = JSONObject(jsonResponse)

            // Used in the Municipality filter
            val uniqueMunicipiNomValues = mutableSetOf<String>()

            val elementsArray = jsonObject.getJSONArray("elements")
            val libraryList = mutableListOf<Library>()

            // Get libraries list from bibliotecavirtual in order to get the library bibliotecavirtual url
            var doc: Document? = null
            try {

                doc =
                    Ksoup.parseGetRequestBlocking(url = "https://bibliotecavirtual.diba.cat/busca-una-biblioteca")
            } catch (exception: Exception) {
                Log.e(
                    "LibraryConverterFactory",
                    "Error getting bibliotecavirtual.diba.cat: $exception"
                )
            }

            for (i in 0 until elementsArray.length()) {

                val libraryElement = elementsArray.getJSONObject(i)

                val imageArray = libraryElement.getJSONArray("imatge")
                val pointId = libraryElement.getString("punt_id")
                val addressName = libraryElement.getString("adreca_nom")
                val description = libraryElement.getString("descripcio")
                val municipalityName =
                    libraryElement.getJSONObject("grup_adreca").getString("municipi_nom")
                uniqueMunicipiNomValues.add(municipalityName)
                val postalCode = libraryElement.getJSONObject("rel_municipis").getString("ine")
                val addressFull =
                    libraryElement.getJSONObject("grup_adreca").getString("adreca_completa")
                val image = if (imageArray.length() > 0) imageArray.getString(0) else ""
                val emails =
                    jsonArrayToStringArray(libraryElement.getJSONArray("email")).ifEmpty { null }
                val phones =
                    jsonArrayToStringArray(libraryElement.getJSONArray("telefon_contacte")).ifEmpty { null }
                val webUrl = libraryElement.getString("url_general").ifEmpty { null }
                val location = libraryElement.getString("localitzacio")
                val (latitude, longitude) = location.takeIf { it.isNotEmpty() }?.split(",")
                    ?.map { it.toDouble() } ?: listOf(0.0, 0.0)


                // Get bibliotecavirtual.diba.cat url
                var bibliotecaVirtualUrl: String? = null
                if (doc != null && emails != null && phones != null) {
                    val emailsTd = doc.select("td.email:contains(${emails[0]})").firstOrNull()
                    val phonesTd = doc.select("td.phone:contains(${phones[0]})").firstOrNull()
                    val nameTd = emailsTd?.siblingElements()?.select("td.name")?.firstOrNull()
                        ?: phonesTd?.siblingElements()?.select("td.name")?.firstOrNull()

                    bibliotecaVirtualUrl = nameTd?.selectFirst("a")?.attr("href")
                }
                // Horaris
                val (timetableHivern, timetableEstiu) = getTimetables(libraryElement)
                val timetable = Timetable(timetableHivern, timetableEstiu)

                val library = Library(
                    id = pointId,
                    adrecaNom = addressName,
                    description = description,
                    municipality = municipalityName,
                    address = addressFull,
                    bibliotecaVirtualUrl = bibliotecaVirtualUrl,
                    emails = emails,
                    phones = phones,
                    webUrl = webUrl,
                    location = listOf(latitude, longitude),
                    image = image,
                    timetable = timetable,
                    postalCode = postalCode
                )
                libraryList.add(library)
            }

            Log.d("LibraryConverterFactory", "Library list size: ${libraryList.size}")
            val response = LibraryResponse(libraryList, uniqueMunicipiNomValues.toList())
            response
        }
    }

    private fun jsonArrayToStringArray(jsonArray: JSONArray): List<String> {
        val stringList = mutableListOf<String>()
        for (i in 0 until jsonArray.length()) {
            stringList.add(jsonArray.getString(i))
        }
        return stringList
    }

    private fun getTimetables(jsonObject: JSONObject): Pair<SeasonTimeTable, SeasonTimeTable> {
        val (dateIntervalHivern, dateIntervalEstiu) = getDateIntervals(jsonObject)
        val hivernTimeTable = getTimetable(jsonObject, Season.WINTER, dateIntervalHivern)
        val estiuTimeTable = getTimetable(jsonObject, Season.SUMMER, dateIntervalEstiu)
        return Pair(hivernTimeTable, estiuTimeTable)
    }

    private fun getTimetable(
        jsonObject: JSONObject,
        season: Season,
        dateInterval: DateInterval
    ): SeasonTimeTable {

        var estacio = "hivern"

        if (season == Season.SUMMER) {
            estacio = "estiu"
        }


        val timeIntervalsDilluns = getTimeIntervals(jsonObject, estacio, "dilluns")
        val timeIntervalsDimarts = getTimeIntervals(jsonObject, estacio, "dimarts")
        val timeIntervalsDimecres = getTimeIntervals(jsonObject, estacio, "dimecres")
        val timeIntervalsDijous = getTimeIntervals(jsonObject, estacio, "dijous")
        val timeIntervalsDivendres = getTimeIntervals(jsonObject, estacio, "divendres")
        val timeIntervalsDissabte = getTimeIntervals(jsonObject, estacio, "dissabte")
        val timeIntervalsDiumenge = getTimeIntervals(jsonObject, estacio, "diumenge")

        val observationsHtml = jsonObject.getString("observacions_$estacio").ifEmpty { null }

        val weekTimetableDeProva = SeasonTimeTable(
            start = dateInterval.from,
            end = dateInterval.to,
            mapOf(
                DayOfWeek.MONDAY to DayTimeTable(timeIntervalsDilluns ?: listOf()),
                DayOfWeek.TUESDAY to DayTimeTable(timeIntervalsDimarts ?: listOf()),
                DayOfWeek.WEDNESDAY to DayTimeTable(timeIntervalsDimecres ?: listOf()),
                DayOfWeek.THURSDAY to DayTimeTable(timeIntervalsDijous ?: listOf()),
                DayOfWeek.FRIDAY to DayTimeTable(timeIntervalsDivendres ?: listOf()),
                DayOfWeek.SATURDAY to DayTimeTable(timeIntervalsDissabte ?: listOf()),
                DayOfWeek.SUNDAY to DayTimeTable(timeIntervalsDiumenge ?: listOf())
            ),
            season = season,
            // If observationsHTML is not null parse it, if not let it null
            observation = observationsHtml?.let { getObservacionsEstacio(jsonObject, estacio) }
        )

        return weekTimetableDeProva

    }

    private fun getTimeIntervals(
        jsonObject: JSONObject,
        estacio: String,
        day: String
    ): List<TimeInterval>? {


        val timeintervalString =
            jsonObject.getString(String.format("horari_%s_%s", estacio, day)).lowercase()


        val regexMonths =
            "(gener|febrer|març|abril|maig|juny|juliol|agost|setembre|octubre|novembre|desembre)".toRegex()
        val regexDayOfWeek =
            "(dilluns|dimarts|dimecres|dijous|divendres|dissabte|diumenge)".toRegex()

        // Timetable contains text
        if (regexMonths.containsMatchIn(timeintervalString) || regexDayOfWeek.containsMatchIn(
                timeintervalString
            )
        ) {
            val timeInterval = TimeInterval(null, null, timeintervalString)
            val timeIntervalsList = mutableListOf<TimeInterval>()
            timeIntervalsList.add(timeInterval)
            return timeIntervalsList
        }

        // If its closed
        if (timeintervalString.contains("tancat")) {
            return null

        }

        // If the string is blank
        if (timeintervalString.isEmpty()) {
            return null
        }


        // If text contains matí o tarda remove it
        if (timeintervalString.contains("matí") || timeintervalString.contains(" tarda")) timeintervalString.replace(
            "matí",
            ""
        ).replace("tarda", "")


        // Format timeintervals
        val regexTime = """(\d+)([:.,])?(\d+)?""".toRegex()
        val timeIntervalsStrings = regexTime.findAll(timeintervalString).map { it.value }.chunked(2)


        val timeTimeIntervals = mutableListOf<TimeInterval>()
        for (timeIntervalString in timeIntervalsStrings) {
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
            timeTimeIntervals.add(timeInterval)

        }

        return timeTimeIntervals
    }


    private fun parseTime(timeString: String): LocalTime? {
        return try {
            var output = timeString
            if (output.length < 3) {
                output += ":00"
            }

            LocalTime.parse(output.replace(".", ":"), timeFormatter)
        } catch (e: DateTimeParseException) {
            throw RuntimeException("Time parsing failed")
        }
    }


    private fun parseMonth(monthName: String): Int {
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

    private fun getObservacionsEstacio(jsonObject: JSONObject, estacio: String): String {

        val htmlString = jsonObject.getString("observacions_$estacio")
        val doc = Ksoup.parse(htmlString)
        val span = doc.selectFirst("p")?.text() ?: return ""

        return span
    }

    private fun getDateIntervals(jsonObject: JSONObject): Pair<DateInterval, DateInterval> {

        fun getIniciHorariEstacio(estacio: String): Pair<Int, Int> {
            val htmlStringEstacio = jsonObject.getString("inici_horari_$estacio")
            val docEstiu = Ksoup.parse(htmlStringEstacio)
            val span = docEstiu.selectFirst("span")
            val day: Int
            val month: Int
            if (span != null && span.text().isNotEmpty()) {
                val dateParts = span.text().split(" ")
                day = dateParts[0].toInt()
                month = parseMonth(span.text())
                return Pair(day, month)

            } else {
                return Pair(1, 1)
            }
        }

        val (dayComencaHivern, monthComencaHivern) = getIniciHorariEstacio("hivern")
        val (dayComencaEstiu, monthComencaEstiu) = getIniciHorariEstacio("estiu")


        val currentDate = LocalDate.now()
        val summerStartDate = LocalDate.of(currentDate.year, monthComencaEstiu, dayComencaEstiu)
        val winterStartDate = LocalDate.of(currentDate.year, monthComencaHivern, dayComencaHivern)
        var yearComencaHivern = currentDate.year
        var yearTerminaHivern = currentDate.year
        var yearComencaEstiu = currentDate.year
        var yearTerminaEstiu = currentDate.year

        // If current date is between summer start and winter start. We are in summer
        if (currentDate >= summerStartDate && currentDate <= winterStartDate) {
            yearTerminaEstiu += 1
        } else {
            if (currentDate.month.value <= monthComencaEstiu) {
                yearComencaHivern -= 1
            } else {

                yearTerminaHivern += 1
                yearComencaEstiu += 1
                yearTerminaEstiu += 1
            }
        }

        val comencaHivern = LocalDate.of(yearComencaHivern, monthComencaHivern, dayComencaHivern)
        val terminaHivern = LocalDate.of(yearTerminaHivern, monthComencaEstiu, dayComencaEstiu)
        val comencaEstiu = LocalDate.of(yearComencaEstiu, monthComencaEstiu, dayComencaEstiu)
        val terminaEstiu = LocalDate.of(yearTerminaEstiu, monthComencaHivern, dayComencaHivern)

        return Pair(
            DateInterval(comencaHivern, terminaHivern),
            DateInterval(comencaEstiu, terminaEstiu)
        )


    }

    companion object {
        fun create(): LibraryConverterFactory {
            return LibraryConverterFactory()
        }
    }
}