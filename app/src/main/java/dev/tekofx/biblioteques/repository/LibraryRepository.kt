package dev.tekofx.biblioteques.repository

import dev.tekofx.biblioteques.call.HolidayService
import dev.tekofx.biblioteques.call.LibraryService
import dev.tekofx.biblioteques.dto.LibraryResponse
import dev.tekofx.biblioteques.model.HolidayDay
import dev.tekofx.biblioteques.model.library.Library
import retrofit2.awaitResponse
import java.time.LocalDate

class LibraryRepository(
    private val libraryService: LibraryService,
    private val holidayService: HolidayService
) {
    suspend fun getLibraries(): LibraryResponse {
        val year = LocalDate.now().year
        val localHolidayDaysUrl =
            "https://analisi.transparenciacatalunya.cat/resource/b4eh-r8up.json?\$query=SELECT\n" +
                    "  `any_calendari`,\n" +
                    "  `data`,\n" +
                    "  `ajuntament_o_nucli_municipal`,\n" +
                    "  `codi_municipi_ine`,\n" +
                    "  `festiu`\n" +
                    "WHERE\n" +
                    "  (`any_calendari` = \"2024\")\n" +
                    "  AND (caseless_starts_with(`codi_municipi_ine`, \"08\")\n" +
                    "         AND (caseless_ne(`ajuntament_o_nucli_municipal`, \"null\")\n" +
                    "                AND caseless_ne(\n" +
                    "                  `ajuntament_o_nucli_municipal`,\n" +
                    "                  \"C. A. de Catalunya\"\n" +
                    "                )))\n" +
                    "ORDER BY `data` ASC NULL LAST"


        val cataloniaHolidayDaysUrl =
            "https://analisi.transparenciacatalunya.cat/resource/8qnu-agns.json?\$query=SELECT `codi`, `any`, `data`, `nom_del_festiu` WHERE `any` IN (\"$year\")"
        val librariesResponse = libraryService.getLibraries().awaitResponse()
        val localHolidayDaysResponse = holidayService.getJson(localHolidayDaysUrl).awaitResponse()
        val cataloniaHolidayDaysResponse =
            holidayService.getJson(cataloniaHolidayDaysUrl).awaitResponse()


        val libraries = librariesResponse.body()?.elements ?: emptyList()
        val municipalities = librariesResponse.body()?.municipalities ?: emptyList()
        val localHolidayDays = localHolidayDaysResponse.body()?.body ?: emptyList()
        val cataloniaHolidayDays = cataloniaHolidayDaysResponse.body()?.body ?: emptyList()

        val librariesWithHolidays =
            addHolidaysToLibraries(libraries, localHolidayDays, cataloniaHolidayDays)

        librariesWithHolidays.forEach {
            println(it.municipality)
            println(it.holidays)
        }

        return LibraryResponse(libraries, municipalities)


    }

    /**
     * Adds to a each [Library] in a List all the [HolidayDay] that affects that [Library]
     * @param libraries: List of [Library]
     * @param localHolidayDays: List of [HolidayDay]
     * @return List of [Library] with the [HolidayDay] of each [Library]
     */
    private fun addHolidaysToLibraries(
        libraries: List<Library>,
        localHolidayDays: List<HolidayDay>,
        cataloniaHolidayDays: List<HolidayDay>
    ): List<Library> {
        libraries.forEach { library: Library ->
            val holy = localHolidayDays.filter { holiday ->
                library.postalCode == holiday.postalCode
            }
            library.holidays = holy.plus(cataloniaHolidayDays)
        }
        return libraries
    }
}