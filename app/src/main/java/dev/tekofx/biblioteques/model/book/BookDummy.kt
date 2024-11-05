package dev.tekofx.biblioteques.model.book

val bookDummy = Book(
    id = 0,
    title = "Aleación de Ley",
    author = "Sanderson, Brandon",
    image = "",
    publication = "Barcelona : Nova, junio 2024",
    temporalUrl = "test",
    bookCopies = arrayListOf(
        BookCopy(
            location = " ST. POL DE MAR",
            signature = "JN San",
            status = "VENÇ EL 28-11-24",
            notes = "V. 2"
        ),
        BookCopy(
            location = " BCN CV.Francesca Bonnemaison",
            signature = "N San",
            status = "Disponible",
            notes = "V. 3"
        )
    )
)