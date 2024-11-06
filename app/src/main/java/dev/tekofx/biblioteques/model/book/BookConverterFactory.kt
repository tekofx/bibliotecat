package dev.tekofx.biblioteques.model.book

import android.util.Log
import com.fleeksoft.ksoup.Ksoup
import com.fleeksoft.ksoup.nodes.Document
import com.fleeksoft.ksoup.select.Elements
import dev.tekofx.biblioteques.dto.BookResponse
import dev.tekofx.biblioteques.model.StatusColor
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.reflect.Type


class BookConverterFactory : Converter.Factory() {

    override fun responseBodyConverter(
        type: Type, annotations: Array<Annotation>, retrofit: Retrofit
    ): Converter<ResponseBody, BookResponse> {
        return Converter { responseBody ->

            val responseBodyString = responseBody.string()
            val doc: Document = Ksoup.parse(responseBodyString)


            val notResultsH2Element =
                doc.select("h2")
                    .firstOrNull() { it.text().contains("no hi ha resultats", ignoreCase = true) }

            val bibInfoLabelElement = doc.select("td.bibInfoLabel").firstOrNull()

            if (notResultsH2Element != null) {
                Log.d("BookConverterFactory", "Not book found")
                BookResponse(responseBodyString, emptyList(), emptyList(), 0)
            } else if (bibInfoLabelElement != null) {
                val bookCopies = constructBookCopies(doc)
                val bookDetails = constructBookDetails(doc)
                Log.d("BookConverterFactory", "Book details")
                BookResponse(responseBodyString, emptyList(), bookCopies, 0, bookDetails)
            } else {
                val books = constructBooks(doc)
                val totalBooks = getTotalBooks(doc)
                Log.d("BookConverterFactory", "Books list")
                BookResponse(responseBodyString, books, emptyList(), totalBooks)
            }

        }
    }

    private fun constructBookDetails(doc: Document): BookDetails {

        val editionElement =
            doc.select("td.bibInfoLabel").firstOrNull { it.text() == "Edició" }
                ?.nextElementSibling()

        val descriptionElement =
            doc.select("td.bibInfoLabel").firstOrNull { it.text() == "Descripció" }
                ?.nextElementSibling()
        val synopsisElement =
            doc.select("td.bibInfoLabel").firstOrNull { it.text() == "Sinopsi" }
                ?.nextElementSibling()

        val isbnElement =
            doc.select("td.bibInfoLabel").firstOrNull { it.text() == "ISBN" }
                ?.nextElementSibling()

        val permanentUrlElement = doc.selectFirst("a#recordnum")

        val edition = editionElement?.text()
        val description = descriptionElement?.text()
        val synopsis = synopsisElement?.text()
        val isbn = isbnElement?.text()
        val permanentUrl = permanentUrlElement?.attr("href")

        return BookDetails(
            edition = edition,
            description = description,
            synopsis = synopsis,
            isbn = isbn,
            permanentUrl = permanentUrl
        )
    }

    private fun getTotalBooks(doc: Document): Int {
        val divElement = doc.selectFirst("div.browseSearchtoolMessage")
        val total = divElement?.text()?.split(" ")?.get(0)?.toInt() ?: 0
        return total
    }

    private fun constructBookCopies(doc: Document): List<BookCopy> {
        val trElements = doc.select("tr.bibItemsEntry")
        val bookCopies = arrayListOf<BookCopy>()
        for (x in trElements) {
            println("w")
            val tdElements = x.select("td")
            val location = tdElements[0].text()
            if (location.isNotEmpty()) {
                val signature = tdElements[1].text()
                val status = tdElements[2].text()
                var notes: String? = tdElements[3].text()
                if (notes!!.isEmpty()) {
                    notes = null
                }

                val statusColor: StatusColor = when {
                    status.contains("Disponible", ignoreCase = true) -> StatusColor.GREEN
                    status.contains("Venç", ignoreCase = true) -> StatusColor.YELLOW
                    else -> StatusColor.RED
                }


                bookCopies.add(
                    BookCopy(
                        location = location,
                        signature = signature,
                        status = status,
                        notes = notes,
                        statusColor = statusColor
                    )
                )
            }
        }
        return bookCopies

    }

    fun constructBooks(doc: Document): List<Book> {
        val bookList = arrayListOf<Book>()

        val bookElements: Elements = doc.select("td.briefCitRow")
        var index = 0
        for (bookElement in bookElements) {
            val descriptionElement = bookElement.selectFirst("div.descript")
            val titleElement = descriptionElement?.selectFirst("span.titular")?.selectFirst("a")
            val imageElement = bookElement.selectFirst("div.brief_portada")?.selectFirst("img")

            val descriptionFields = descriptionElement.toString().split("<br>")

            val url = titleElement?.attr("href")
            val author = descriptionFields[2].trim()
            val publication = descriptionFields[3].split("<!--")[0].trim()

            if (titleElement != null && imageElement != null) {
                bookList.add(
                    Book(
                        id = index,
                        title = titleElement.text(),
                        author = author,
                        image = imageElement.attr("src"),
                        publication = publication,
                        bookCopies = arrayListOf(),
                        temporalUrl = url!!
                    )
                )
                index++
            }
        }
        return bookList
    }

    companion object {
        fun create(): BookConverterFactory {
            return BookConverterFactory()
        }
    }
}