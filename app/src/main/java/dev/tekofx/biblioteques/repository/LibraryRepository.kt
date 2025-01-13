package dev.tekofx.biblioteques.repository

import android.util.Log
import dev.tekofx.biblioteques.call.HolidayService
import dev.tekofx.biblioteques.call.LibraryService
import dev.tekofx.biblioteques.dto.HolidayResponse
import dev.tekofx.biblioteques.dto.LibraryResponse
import dev.tekofx.biblioteques.model.HolidayDay
import dev.tekofx.biblioteques.model.library.Library
import org.json.JSONException
import retrofit2.Response
import retrofit2.awaitResponse
import java.time.LocalDate

class LibraryRepository(
    private val libraryService: LibraryService,
    private val holidayService: HolidayService
) {
    suspend fun getLibraries(): LibraryResponse {
        try {

            val year = LocalDate.now().year

            val librariesResponse = libraryService.getLibraries().awaitResponse()
            val localHolidayDaysResponse = getLocalHolidayDaysResponse(year)
            val cataloniaHolidayDaysResponse = getCataloniaHolidayDaysResponse(year)

            val libraries = librariesResponse.body()?.elements ?: emptyList()
            val municipalities = librariesResponse.body()?.municipalities ?: emptyList()
            val localHolidayDays = localHolidayDaysResponse?.body()?.body ?: emptyList()
            val cataloniaHolidayDays = cataloniaHolidayDaysResponse?.body()?.body ?: emptyList()

            val librariesWithHolidays =
                addHolidaysToLibraries(libraries, localHolidayDays, cataloniaHolidayDays)

            return LibraryResponse(librariesWithHolidays, municipalities)
        } catch (e: JSONException) {
            Log.d("LibraryRepository", "Error getting libraries", e)
            return LibraryResponse(emptyList(), emptyList())
        }
    }

    /**
     * Gets the local holiday days from the API
     * @param year: Int
     * @return Response<HolidayResponse>?
     */
    private suspend fun getLocalHolidayDaysResponse(year: Int): Response<HolidayResponse>? {
        val localHolidayDaysUrl =
            "https://analisi.transparenciacatalunya.cat/resource/b4eh-r8up.json?\$query=SELECT\n" +
                    "  `any_calendari`,\n" +
                    "  `data`,\n" +
                    "  `ajuntament_o_nucli_municipal`,\n" +
                    "  `codi_municipi_ine`,\n" +
                    "  `festiu`\n" +
                    "WHERE\n" +
                    "  (`any_calendari` = \"$year\")\n" +
                    "  AND (caseless_starts_with(`codi_municipi_ine`, \"08\")\n" +
                    "  AND (caseless_ne(`ajuntament_o_nucli_municipal`, \"null\")\n" +
                    "  AND caseless_ne(`ajuntament_o_nucli_municipal`, \"C. A. de Catalunya\"\n)))" +
                    "ORDER BY `data` ASC NULL LAST"

        val localHolidayDaysResponse = try {
            holidayService.getJson(localHolidayDaysUrl).awaitResponse()
        } catch (e: Exception) {
            Log.d("LibraryRepository", "Error getting local holiday days", e)
            null
        }

        return localHolidayDaysResponse
    }

    private suspend fun getCataloniaHolidayDaysResponse(year: Int): Response<HolidayResponse>? {
        val cataloniaHolidayDaysUrl =
            "https://analisi.transparenciacatalunya.cat/resource/8qnu-agns.json?\$query=SELECT `codi`, `any`, `data`, `nom_del_festiu` WHERE `any` IN (\"$year\")"

        val cataloniaHolidayDaysResponse = try {
            holidayService.getJson(cataloniaHolidayDaysUrl).awaitResponse()
        } catch (e: Exception) {
            Log.d("LibraryRepository", "Error getting catalonia holiday days", e)
            null
        }

        return cataloniaHolidayDaysResponse
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