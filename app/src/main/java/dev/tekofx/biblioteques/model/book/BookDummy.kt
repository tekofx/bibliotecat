package dev.tekofx.biblioteques.model.book

import dev.tekofx.biblioteques.model.StatusColor

val bookDummy = Book(
    id = 0,
    title = "Aleación de Ley",
    author = "Sanderson, Brandon",
    image = "",
    publication = "Barcelona : Nova, junio 2024",
    url = "test",
    bookCopies = arrayListOf(
        BookCopy(
            location = " ST. POL DE MAR",
            signature = "JN San",
            status = "VENÇ EL 28-11-24",
            notes = "V. 2",
            statusColor = StatusColor.YELLOW,
            availability = BookCopyAvailability.CAN_RESERVE,
            bibliotecaVirtualUrl = ""
        ),
        BookCopy(
            location = " BCN CV.Francesca Bonnemaison",
            signature = "N San",
            status = "Disponible",
            notes = "V. 3",
            statusColor = StatusColor.GREEN,
            availability = BookCopyAvailability.AVAILABLE,
            bibliotecaVirtualUrl = ""
        ),
        BookCopy(
            location = " BCN CV.Francesca Bonnemaison",
            signature = "N San",
            status = "Reserva a sala",
            notes = "V. 3",
            statusColor = StatusColor.RED,
            availability = BookCopyAvailability.NOT_AVAILABLE,
            bibliotecaVirtualUrl = ""
        )
    )
)