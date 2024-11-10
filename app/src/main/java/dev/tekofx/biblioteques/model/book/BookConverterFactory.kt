package dev.tekofx.biblioteques.model.book

import android.util.Log
import com.fleeksoft.ksoup.Ksoup
import com.fleeksoft.ksoup.nodes.Document
import com.fleeksoft.ksoup.nodes.Element
import com.fleeksoft.ksoup.select.Elements
import dev.tekofx.biblioteques.dto.BookResponse
import dev.tekofx.biblioteques.model.BookResult
import dev.tekofx.biblioteques.model.BookResults
import dev.tekofx.biblioteques.model.GeneralResult
import dev.tekofx.biblioteques.model.GeneralResults
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

            val bibPagerElement = doc.select("div.bibPager").firstOrNull()

            val browseHeaderEntriesElement = doc.select("td.browseHeaderEntries").firstOrNull()

            if (notResultsH2Element != null) {
                Log.d("BookConverterFactory", "Not book found")
                throw Error()
            } else if (browseHeaderEntriesElement != null) {
                Log.d("BookConverterFactory", "Result Search")
                val generalResults = constructGeneralResults(doc)
                val pages = getPages(doc)
                println(pages.toString())
                BookResponse(
                    body = responseBodyString,
                    pages = pages,
                    results = generalResults

                )
            } else if (bibInfoLabelElement != null) {
                val bookDetails = constructBookDetails(doc)
                val bookCopies = constructBookCopies(doc)
                val bookResults = constructBookResults(doc)

                Log.d("BookConverterFactory", "Book details")
                BookResponse(
                    results = bookResults,
                    bookDetails = bookDetails,
                    bookCopies = bookCopies
                )
            } else {

                val totalBooks = getTotalSearchResults(doc)
                val bookResults = constructBookResults(doc)

                Log.d("BookConverterFactory", "Search with multiple books")
                BookResponse(
                    body = responseBodyString,
                    totalBooks = totalBooks,
                    results = bookResults
                )
            }

        }
    }


    private fun getPages(doc: Document): List<String> {
        val tdElement = doc.selectFirst("td.browsePager") ?: return emptyList()
        val liElements = tdElement.select("li.wpPagerList")
        val list = arrayListOf<String>()
        for (liElement in liElements) {
            if (liElement.text().contains("Anterior") || liElement.text().contains("Següent")) {
                continue
            }
            val aElement = liElement.selectFirst("a") ?: continue
            val url = aElement.attr("href")
            list.add(url)
        }
        return list

    }

    private fun constructGeneralResults(doc: Document): GeneralResults {
        val browseEntryDataElements = doc.select("tr.browseEntry")

        val generalResultsList = arrayListOf<GeneralResult>()
        val pages = getPages(doc)
        val numItems = getTotalSearchResults(doc)



        for (browseEntrydataElement in browseEntryDataElements) {
            val text = browseEntrydataElement.select("td.browseEntryData").text()
            val url =
                browseEntrydataElement.select("a").last()?.attr("href")
                    ?.replace("https://aladi.diba.cat/", "") ?: continue
            val entries =
                browseEntrydataElement.selectFirst("td.browseEntryEntries")?.text()?.toInt()
                    ?: continue

            val id = browseEntrydataElement.selectFirst("td.browseEntryNum")?.text()?.toInt()
                ?: continue
            generalResultsList.add(
                GeneralResult(
                    id = id,
                    text = text,
                    url = url,
                    numEntries = entries,
                )
            )
        }
        return GeneralResults(
            items = generalResultsList.toList(),
            pages = pages,
            numItems = numItems

        )
    }

    private fun contructBookFromBookDetails(doc: Document): Book {
        val titleElement =
            doc.select("td.bibInfoLabel").firstOrNull { it.text() == "Títol" }
                ?.nextElementSibling()

        val authorElement =
            doc.select("td.bibInfoLabel").firstOrNull { it.text() == "Autor/Artista" }
                ?.nextElementSibling()

        val imageElement = doc.selectFirst("div.fitxa_imatge")?.selectFirst("img")
        val publicationElement =
            doc.select("td.bibInfoLabel").firstOrNull { it.text() == "Publicació" }
                ?.nextElementSibling()

        val permanentLinkElement = doc.selectFirst("a.recordnum")


        val title = titleElement?.text()?.split("/")?.get(0) ?: ""
        val author = authorElement?.text() ?: ""
        val image = imageElement?.attr("src") ?: ""
        val publication = publicationElement?.text() ?: ""
        val permanentLink = permanentLinkElement?.attr("href") ?: ""
        val bookDetails = constructBookDetails(doc)
        val bookCopies = constructBookCopies(doc)


        return Book(
            id = 0,
            title = title,
            author = author,
            image = image,
            publication = publication,
            temporalUrl = permanentLink,
            bookDetails = bookDetails,
            bookCopies = bookCopies
        )
    }

    private fun constructBookResultsFromBookDetails(doc: Document): BookResults {
        val titleElement =
            doc.select("td.bibInfoLabel").firstOrNull { it.text() == "Títol" }
                ?.nextElementSibling()

        val authorElement =
            doc.select("td.bibInfoLabel").firstOrNull { it.text() == "Autor/Artista" }
                ?.nextElementSibling()

        val imageElement = doc.selectFirst("div.fitxa_imatge")?.selectFirst("img")
        val publicationElement =
            doc.select("td.bibInfoLabel").firstOrNull { it.text() == "Publicació" }
                ?.nextElementSibling()

        val permanentLinkElement = doc.selectFirst("a.recordnum")


        val title = titleElement?.text()?.split("/")?.get(0) ?: ""
        val author = authorElement?.text() ?: ""
        val image = imageElement?.attr("src") ?: ""
        val publication = publicationElement?.text() ?: ""
        val permanentLink = permanentLinkElement?.attr("href") ?: ""

        val bookResults = BookResults(
            items = listOf(
                BookResult(
                    id = 0,
                    title = title,
                    url = permanentLink,
                    publication = publication,
                    author = author,
                    image = image
                )
            ),
            pages = emptyList(),
            numItems = 1
        )
        return bookResults

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

    /**
     * Gets the number of results from a search
     * @param doc
     *
     * @return Number of results for a search
     */
    private fun getTotalSearchResults(doc: Document): Int {
        val divElement = doc.selectFirst("td.browseHeaderData")
        val total = divElement?.text()?.replace(")", "")?.split(" de ")?.last()?.toInt() ?: 0
        return total
    }


    private fun constructBookCopies(element: Element): List<BookCopy> {
        val trElements = element.select("tr.bibItemsEntry")
        val bookCopies = arrayListOf<BookCopy>()
        for (x in trElements) {
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

    private fun constructBookResults(doc: Document): BookResults {

        val bookResultList = arrayListOf<BookResult>()
        val pages = getPages(doc)
        val numItems = getTotalSearchResults(doc)

        val bookElements: Elements = doc.select("td.briefCitRow")
        for (bookElement in bookElements) {
            val descriptionElement = bookElement.selectFirst("div.descript")
            val titleElement = descriptionElement?.selectFirst("span.titular")?.selectFirst("a")
            val imageElement = bookElement.selectFirst("div.brief_portada")?.selectFirst("img")

            if (titleElement != null && imageElement != null) {
                val descriptionFields = descriptionElement.toString().split("<br>")
                val url = titleElement.attr("href")
                val author = descriptionFields[2].trim()
                val publication = descriptionFields[3].split("<!--")[0].trim()

                val id = url.split("&").last().split("%")[0].toInt()
                val title = titleElement.text()
                val image = imageElement.attr("src")


                bookResultList.add(
                    BookResult(
                        id = id,
                        title = title,
                        author = author,
                        publication = publication,
                        url = url,
                        image = image,
                    )
                )

            }
        }
        return BookResults(
            items = bookResultList.toList(),
            pages = pages,
            numItems = numItems
        )
    }

    companion object {
        fun create(): BookConverterFactory {
            return BookConverterFactory()
        }
    }
}