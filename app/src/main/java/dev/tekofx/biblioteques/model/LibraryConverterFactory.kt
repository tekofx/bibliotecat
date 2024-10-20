package dev.tekofx.biblioteques.model

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

    val timeFormatter = DateTimeFormatter.ofPattern("[H:mm][HH:mm]")


    override fun responseBodyConverter(
        type: Type, annotations: Array<Annotation>, retrofit: Retrofit
    ): Converter<ResponseBody, LibraryResponse>? {
        return Converter { responseBody ->
            val jsonResponse = responseBody.string()
            val jsonObject = JSONObject(jsonResponse)

            val elementsArray = jsonObject.getJSONArray("elements")
            val libraryList = ArrayList<Library>()

            // Get libraries list from bibliotecavirtual in order to get the library bibliotecavirtual url
            val doc: Document =
                Ksoup.parseGetRequestBlocking(url = "https://bibliotecavirtual.diba.cat/busca-una-biblioteca")
            for (i in 0 until elementsArray.length()) {
                val libraryElement = elementsArray.getJSONObject(i)
                val imatgeArray = libraryElement.getJSONArray("imatge")
                val puntId = libraryElement.getString("punt_id")
                val adrecaNom = libraryElement.getString("adreca_nom")
                val descripcio = libraryElement.getString("descripcio")
                val municipiNom =
                    libraryElement.getJSONObject("grup_adreca").getString("municipi_nom")
                val imatge = if (imatgeArray.length() > 0) imatgeArray.getString(0) else ""
                val emails = jsonArrayToStringArray(libraryElement.getJSONArray("email"))

                // bibliotecavirtual.diba.cat url
                val emailsTd = doc.selectFirst("td.email:contains(${emails[0]})")
                val nameTd = emailsTd?.siblingElements()?.select("td.name")
                    ?.firstOrNull()
                val bibliotecaVirtualUrl = nameTd?.getElementsByTag("a")?.attr("href")

                // Horaris
                val (timetableHivern, timetableEstiu) = getTimetables(libraryElement)
                val actualDate = LocalDate.now()

                var timeTableActual = timetableHivern;
                if (actualDate >= timetableEstiu.dateInterval.from && actualDate <= timetableEstiu.dateInterval.to) {
                    timeTableActual = timetableEstiu
                }


                val library = Library(
                    puntId = puntId,
                    adrecaNom = adrecaNom,
                    descripcio = descripcio,
                    municipiNom = municipiNom,
                    bibliotecaVirtualUrl = bibliotecaVirtualUrl,
                    emails = emails,
                    imatge = imatge,
                    timetableEstiu = timetableEstiu,
                    timetableHivern = timetableHivern,
                    timetableActual = timeTableActual
                )
                // Rellena los demás atributos según sea necesario
                libraryList.add(library)
            }

            val response = LibraryResponse(libraryList)
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

    private fun getTimetables(jsonObject: JSONObject): Pair<Timetable, Timetable> {
        val (dateIntervalHivern, dateIntervalEstiu) = getDateIntervals(jsonObject)
        val hivernTimeTable = getTimetable(jsonObject, "hivern", dateIntervalHivern)
        val estiuTimeTable = getTimetable(jsonObject, "estiu", dateIntervalEstiu)
        return Pair(hivernTimeTable, estiuTimeTable)
    }

    private fun getTimetable(
        jsonObject: JSONObject,
        estacio: String,
        dateInterval: DateInterval
    ): Timetable {

        val observacions = getObservacionsEstacio(jsonObject, estacio)
        val timeIntervalsDilluns = getTimeIntervals(jsonObject, estacio, "dilluns")
        val timeIntervalsDimarts = getTimeIntervals(jsonObject, estacio, "dimarts")
        val timeIntervalsDimecres = getTimeIntervals(jsonObject, estacio, "dimecres")
        val timeIntervalsDijous = getTimeIntervals(jsonObject, estacio, "dijous")
        val timeIntervalsDivendres = getTimeIntervals(jsonObject, estacio, "divendres")
        val timeIntervalsDissabte = getTimeIntervals(jsonObject, estacio, "dissabte")
        val timeIntervalsDiumenge = getTimeIntervals(jsonObject, estacio, "diumenge")

        // TODO: Change to LocalDate.now()
        val actualDateOfWeek = LocalDate.of(2024, 10, 21).dayOfWeek

        val dayOfWeekToTimeIntervalMap = mapOf(
            DayOfWeek.MONDAY to timeIntervalsDilluns,
            DayOfWeek.TUESDAY to timeIntervalsDimarts,
            DayOfWeek.WEDNESDAY to timeIntervalsDimecres,
            DayOfWeek.THURSDAY to timeIntervalsDijous,
            DayOfWeek.FRIDAY to timeIntervalsDivendres,
            DayOfWeek.SATURDAY to timeIntervalsDissabte,
            DayOfWeek.SUNDAY to timeIntervalsDiumenge
        )


        val actualDateInterval =
            dayOfWeekToTimeIntervalMap[actualDateOfWeek] ?: timeIntervalsDilluns


        val timetableDeProva = Timetable(
            dateInterval = dateInterval,
            actualDateInterval = actualDateInterval,
            estacio = estacio,
            observacions = observacions,
            dilluns = timeIntervalsDilluns,
            dimarts = timeIntervalsDimarts,
            dimecres = timeIntervalsDimecres,
            dijous = timeIntervalsDijous,
            divendres = timeIntervalsDivendres,
            dissabte = timeIntervalsDissabte,
            diumenge = timeIntervalsDiumenge
        )

        return timetableDeProva

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
//            val timeInterval = TimeInterval(null, null, null)
//            val timeIntervalsList = mutableListOf<TimeInterval>()
//            timeIntervalsList.add(timeInterval)
//            return timeIntervalsList
            return null

        }

        // If the string is blank
        if (timeintervalString.isEmpty()) {
//            val timeInterval = TimeInterval(null, null, null)
//            val timeIntervalsList = mutableListOf<TimeInterval>()
//            timeIntervalsList.add(timeInterval)
//            return timeIntervalsList
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


        val timeIntervals = mutableListOf<TimeInterval>()
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

        var htmlString = jsonObject.getString("observacions_$estacio")
        val doc = Ksoup.parse(htmlString)
        val span = doc.selectFirst("p")?.text() ?: return ""

        return span
    }

    private fun getDateIntervals(jsonObject: JSONObject): Pair<DateInterval, DateInterval> {

        fun getIniciHorariEstacio(estacio: String): Pair<Int, Int> {
            val htmlStringEstacio = jsonObject.getString("inici_horari_$estacio")
            val docEstiu = Ksoup.parse(htmlStringEstacio)
            val span = docEstiu.selectFirst("span")
            var day = 0;
            var month = 0;
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


        val actualDate = LocalDate.now()
        val summerStartDate = LocalDate.of(actualDate.year, monthComencaEstiu, dayComencaEstiu)
        val winterStartDate = LocalDate.of(actualDate.year, monthComencaHivern, dayComencaHivern)
        var yearComencaHivern = actualDate.year
        var yearTerminaHivern = actualDate.year
        var yearComencaEstiu = actualDate.year
        var yearTerminaEstiu = actualDate.year

        // If actual date is between summer start and winter start. We are in summer
        if (actualDate >= summerStartDate && actualDate <= winterStartDate) {
            yearTerminaEstiu += 1
        } else {
            if (actualDate.month.value <= monthComencaEstiu) {
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