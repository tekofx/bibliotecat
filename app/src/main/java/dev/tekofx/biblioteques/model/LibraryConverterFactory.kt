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
                val timetableEstiu = getTimetable(libraryElement, "estiu")
                val timetableHivern = getTimetable(libraryElement, "hivern")

                val iniciHorariEstiu = timetableEstiu.comenca;
                val iniciHorariHivern = timetableHivern.comenca;
                var timeTableActual = timetableHivern;
                val actualDate = LocalDate.of(2024, 7, 1)

                if (iniciHorariEstiu != null && iniciHorariHivern != null) {
                    if (iniciHorariEstiu.month < actualDate.month && actualDate.month < iniciHorariHivern.month) {
                        println("Horari actual estiu")
                        println("Horari hivern ${actualDate.year}/${iniciHorariHivern.month} - ${actualDate.year + 1}/${iniciHorariEstiu.month}")
                        println("Horari estiu ${actualDate.year}/${actualDate.month} - ${actualDate.year}/${iniciHorariHivern.month}")


                    } else {
                        println("Horari actual hivern ")
                        println("Horari hivern ${actualDate.year}/${actualDate.month} - ${actualDate.year + 1}/${iniciHorariEstiu.month}")
                        println("Horari estiu ${actualDate.year + 1}/${actualDate.month} - ${actualDate.year + 1}/${iniciHorariHivern.month}")
                    }
                }


                println("Inici horari hivern $iniciHorariHivern")
                println("Inici horari estiu $iniciHorariEstiu")
                println("Horari actual $timeTableActual")

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
            println("End of for loop")
            val response = LibraryResponse(libraryList)
            response
        }
    }

    fun jsonArrayToStringArray(jsonArray: JSONArray): List<String> {
        val stringList = mutableListOf<String>()
        for (i in 0 until jsonArray.length()) {
            stringList.add(jsonArray.getString(i))
        }
        return stringList
    }

    private fun getTimetable(jsonObject: JSONObject, estacio: String): Timetable {

        val iniciHorari = getIniciHorari(jsonObject, estacio)
        val observacions = getObservacionsEstacio(jsonObject, estacio)
        val timeIntervalsDilluns = getTimeIntervals(jsonObject, estacio, "dilluns")
        val timeIntervalsDimarts = getTimeIntervals(jsonObject, estacio, "dimarts")
        val timeIntervalsDimecres = getTimeIntervals(jsonObject, estacio, "dimecres")
        val timeIntervalsDijous = getTimeIntervals(jsonObject, estacio, "dijous")
        val timeIntervalsDivendres = getTimeIntervals(jsonObject, estacio, "divendres")
        val timeIntervalsDissabte = getTimeIntervals(jsonObject, estacio, "dissabte")
        val timeIntervalsDijumenge = getTimeIntervals(jsonObject, estacio, "diumenge")

        val timetableDeProva = Timetable(
            comenca = iniciHorari,
            estacio = estacio,
            observacions = observacions,
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

    private fun getTimeIntervals(
        jsonObject: JSONObject,
        estacio: String,
        day: String
    ): List<TimeInterval> {


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

    private fun getIniciHorari(jsonObject: JSONObject, estacio: String): LocalDate? {

        val htmlString = jsonObject.getString("inici_horari_$estacio")

        val doc = Ksoup.parse(htmlString)
        val span = doc.selectFirst("span")
        var day = 0;
        var month = 0;



        if (span != null && span.text().isNotEmpty()) {
            val dateParts = span.text().split(" ")
            day = dateParts[0].toInt()
            month = parseMonth(span.text())

        } else {
            return null
        }
        val actualMonth = LocalDate.now().month
        var year = LocalDate.now().year

        if (month < actualMonth.value) {
            year += 1

        }


        return LocalDate.of(year, month, day)

    }

    companion object {
        fun create(): LibraryConverterFactory {
            return LibraryConverterFactory()
        }
    }
}