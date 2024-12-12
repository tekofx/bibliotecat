package dev.tekofx.biblioteques.repository

import dev.tekofx.biblioteques.call.HolidayService
import dev.tekofx.biblioteques.call.LibraryService
import dev.tekofx.biblioteques.dto.LibraryResponse
import dev.tekofx.biblioteques.model.HolidayDay
import dev.tekofx.biblioteques.model.library.Library
import retrofit2.awaitResponse

class LibraryRepository(
    private val libraryService: LibraryService,
    private val holidayService: HolidayService
) {
    suspend fun getLibraries(): LibraryResponse {
        val year = 2024

        val localHolidayDaysUrl =
            "https://analisi.transparenciacatalunya.cat/resource/b4eh-r8up.json?\$query=SELECT%0A%20%20`any_calendari`%2C%0A%20%20`data`%2C%0A%20%20`ajuntament_o_nucli_municipal`%2C%0A%20%20`codi_municipal`%2C%0A%20%20`codi_municipi_ine`%2C%0A%20%20`pedania`%2C%0A%20%20`festiu`%2C%0A%20%20`codiidescat`%0AWHERE%20`any_calendari`%20IN%20(%22$year%22)"

        val cataloniaHolidayDaysUrl =
            "https://analisi.transparenciacatalunya.cat/resource/8qnu-agns.json?\$query=SELECT%20%60codi%60%2C%20%60any%60%2C%20%60data%60%2C%20%60nom_del_festiu%60%20WHERE%20%60any%60%20IN%20(%22$year%22)"
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