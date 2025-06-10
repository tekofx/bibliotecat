package dev.tekofx.biblioteques.model.library

import dev.tekofx.biblioteques.model.StatusColor
import java.time.LocalDate
import java.time.LocalTime

class Library(
    val id: String,
    val name: String,
    val description: String,
    val municipality: String,
    val address: String,
    val bibliotecaVirtualUrl: String?,
    val emails: List<String>?,
    val phones: List<String>?,
    val webUrl: String?,
    val location: List<Double>,
    var image: String,
    val timetable: Timetable?,
) {


    var libraryStatus: LibraryStatus =
        timetable?.getOpenStatus(LocalDate.now(), LocalTime.now()) ?: LibraryStatus(
            LibraryStatus.Value.MayBeOpen.Unknow, StatusColor.GRAY, "Horari no disponible"
        )


}