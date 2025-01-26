package dev.tekofx.biblioteques.model.book

import android.util.Log
import com.fleeksoft.ksoup.Ksoup
import com.fleeksoft.ksoup.nodes.Document
import com.fleeksoft.ksoup.nodes.Element
import com.fleeksoft.ksoup.select.Elements
import dev.tekofx.biblioteques.R
import dev.tekofx.biblioteques.dto.BookResponse
import dev.tekofx.biblioteques.model.BookResult
import dev.tekofx.biblioteques.model.BookResults
import dev.tekofx.biblioteques.model.GeneralResult
import dev.tekofx.biblioteques.model.GeneralResults
import dev.tekofx.biblioteques.model.SelectItem
import dev.tekofx.biblioteques.model.StatusColor
import dev.tekofx.biblioteques.ui.IconResource
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

            val advancedSearchElement =
                doc.select("h2")
                    .firstOrNull() { it.text().contains("Cerca avançada", ignoreCase = true) }


            val divBrowseSearchTool = doc.select("div.browseSearchtoolMessage")
                .firstOrNull() { it.text().contains("resultats trobats.", ignoreCase = true) }


            val bibInfoLabelElement = doc.select("td.bibInfoLabel").firstOrNull()
            val tdbriefCitRowElement = doc.select("td.briefCitRow").firstOrNull()

            val additionalCopiesElement = doc.select("div.additionalCopies").firstOrNull()
            val browseHeaderEntriesElement = doc.select("td.browseHeaderEntries").firstOrNull()

            when {
                advancedSearchElement != null -> {
                    Log.d("BookConverterFactory", "Get Search Scope")
                    val searchScopes = getSearchScope(doc)
                    BookResponse(
                        body = responseBodyString,
                        searchScopes = searchScopes
                    )
                }

                notResultsH2Element != null -> {
                    Log.d("BookConverterFactory", "Not results")
                    BookResponse()
                }

                browseHeaderEntriesElement != null -> {
                    Log.d("BookConverterFactory", "Result Search")
                    val generalResults = constructGeneralResults(doc)
                    val pages = getPages(doc)
                    BookResponse(
                        body = responseBodyString,
                        pages = pages,
                        results = generalResults
                    )
                }

                additionalCopiesElement != null -> {
                    Log.d("BookConverterFactory", "Additional Book Copies")
                    val bookCopies = constructBookCopies(doc)
                    BookResponse(
                        bookCopies = bookCopies
                    )
                }

                bibInfoLabelElement != null -> {
                    Log.d("BookConverterFactory", "Book details")
                    val book = contructBookFromBookDetails(doc)
                    val bookDetails = constructBookDetails(doc)
                    val bookResults = constructBookResultsFromBookDetails(doc)

                    val thereAreMoreCopies =
                        doc.select("input[type=submit][value='Veure més exemplars o indicar el volum/còpia']")
                            .firstOrNull()

                    Log.d("BookConverterFactory", "Test")

                    BookResponse(
                        book = book,
                        bookDetails = bookDetails,
                        results = bookResults,
                        thereAreMoreCopies = thereAreMoreCopies != null
                    )
                }

                divBrowseSearchTool != null -> {
                    val totalBooks = getTotalSearchResults(doc)
                    val bookResults = constructBookResults(doc)

                    Log.d("BookConverterFactory", "Search with multiple books")
                    BookResponse(
                        body = responseBodyString,
                        totalBooks = totalBooks,
                        results = bookResults
                    )
                }

                tdbriefCitRowElement != null -> {
                    val totalBooks = getTotalSearchResults(doc)
                    val bookResults = constructBookResults(doc)


                    Log.d("BookConverterFactory", "Search by Signature with Book Results")
                    BookResponse(
                        body = responseBodyString,
                        totalBooks = totalBooks,
                        results = bookResults
                    )
                }

                else -> {
                    Log.d("BookConverterFactory", "Else case")
                    throw Error()
                }
            }

        }
    }


    private fun getSearchScope(doc: Document): List<SelectItem> {
        val searchScopes = mutableListOf<SelectItem>()
        val searchScopeElement =
            doc.selectFirst("select#searchscope")?.getElementsByTag("option")
                ?: return emptyList()
        for (searchVal in searchScopeElement) {
            val value = searchVal.attr("value")
            val text = searchVal.text()
            val icon = getIcon(text)
            searchScopes.add(
                SelectItem(
                    text,
                    value,
                    icon
                )
            )
        }
        return searchScopes
    }

    private fun getIcon(value: String): IconResource {
        return when {
            value == "Tot el catàleg" -> IconResource.fromDrawableResource(R.drawable.library_books)
            value == "Subcatàlegs" -> IconResource.fromDrawableResource(R.drawable.library_books)
            value == "Comarques" -> IconResource.fromDrawableResource(R.drawable.location_city)
            value == "Música" -> IconResource.fromDrawableResource(R.drawable.music_note)
            value == "Pel·lícules" -> IconResource.fromDrawableResource(R.drawable.movie)
            value == "Còmics" -> IconResource.fromDrawableResource(R.drawable.comic_bubble)
            value == "Recursos en línia" -> IconResource.fromDrawableResource(R.drawable.public_icon)
            value == "Audiollibres, lletra gran i braille" -> IconResource.fromDrawableResource(R.drawable.books_movies_and_music)
            value == "Bibliobusos" -> IconResource.fromDrawableResource(R.drawable.directions_bus)
            value.startsWith("Bibliobús") -> IconResource.fromDrawableResource(R.drawable.directions_bus)
            else -> IconResource.fromDrawableResource(R.drawable.location_city)
        }
    }

    /**
     * Gets a List containing the URLs of other ResultsPages
     * @return List with urls
     */
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
        var numItems = getTotalSearchResults(doc)
        var index = 0

        for (browseEntrydataElement in browseEntryDataElements) {
            val text = browseEntrydataElement.select("td.browseEntryData").text()
            val url =
                browseEntrydataElement.select("a").last()?.attr("href")
                    ?.replace("https://aladi.diba.cat/", "") ?: continue
            val entries =
                browseEntrydataElement.selectFirst("td.browseEntryEntries")?.text()?.toInt()
                    ?: continue


            val id = browseEntrydataElement.selectFirst("td.browseEntryNum")?.text()?.toInt()
                ?: index++
            generalResultsList.add(
                GeneralResult(
                    id = id,
                    text = text,
                    url = url,
                    numEntries = entries,
                )
            )
        }

        if (numItems == -1) {
            numItems = generalResultsList.size
        }
        return GeneralResults(
            items = generalResultsList.toList(),
            pages = pages,
            numItems = numItems

        )
    }

    private fun contructBookFromBookDetails(doc: Document): Book {
        Log.d("BookConverterFactory", "constructBookFromBookDetails")
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

        val permanentLinkElement = doc.selectFirst("a#recordnum")

        val title = titleElement?.text()?.split("/")?.get(0)?.trim() ?: ""
        val author = authorElement?.text() ?: ""
        val image = imageElement?.attr("src") ?: ""
        val publication = publicationElement?.text()
        val permanentLink = permanentLinkElement?.attr("href") ?: ""
        val bookDetails = constructBookDetails(doc)
        val bookCopies = constructBookCopies(doc)


        return Book(
            id = 0,
            title = title,
            author = author,
            image = image.split("&log=").first(),
            publication = publication,
            url = permanentLink,
            bookDetails = bookDetails,
            bookCopies = bookCopies
        )
    }

    private fun constructBookResultsFromBookDetails(doc: Document): BookResults {
        Log.d("BookConverterFactory", "constructBookResultsFromBookDetails")

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

        val permanentLinkElement = doc.selectFirst("a#recordnum")


        val title = titleElement?.text()?.split("/")?.get(0)?.trim() ?: ""
        val author = authorElement?.text() ?: ""
        val image = imageElement?.attr("src") ?: ""
        val publication = publicationElement?.text()
        val permanentLink = permanentLinkElement?.attr("href") ?: ""

        val bookResults = BookResults(
            items = listOf(
                BookResult(
                    id = 0,
                    title = title,
                    url = permanentLink,
                    publication = publication,
                    author = author,
                    image = image.split("&log=").first()
                )
            ),
            pages = emptyList(),
            numItems = 1
        )
        return bookResults

    }

    private fun constructBookDetails(doc: Document): BookDetails {
        Log.d("BookConverterFactory", "constructBookDetails")

        val editionElement = getElement("Edició", doc)
        val descriptionElement = getElement("Descripció", doc)
        val synopsisElement = getElement("Sinopsi", doc)
        val isbnElement = getElement("ISBN", doc)
        val collectionsElements = getMultipleElements("Col·lecció",doc)
        val topicElements= getMultipleElements("Tema",doc)


        Log.d("BookConverterFactory", "constructBookDetails: Got topicElement")


        val permanentUrlElement = doc.selectFirst("a#recordnum")
        Log.d("BookConverterFactory", "constructBookDetails: Got permanentUrl")


        val edition = editionElement?.text()
        val description = descriptionElement?.text()
        val synopsis = synopsisElement?.text()
        val isbn = isbnElement?.text()
        val permanentUrl = permanentUrlElement?.attr("href")
        val collections = collectionsElements.map { it.text() }
        val topics= topicElements.map { it.text() }


        var bookCopiesUrl: String? = null
        if (permanentUrl != null) {
            val regex = Regex("record=([^~]+)")
            val matchResult = regex.find(permanentUrl)
            val bvalue = matchResult?.groupValues?.get(1)
            val value = bvalue?.replace("b", "")
            bookCopiesUrl =
                "search~S171*cat?/.b$value/.b$value/1,1,1,B/holdings~$value&FF=&1,0,"
        }

        return BookDetails(
            edition = edition,
            description = description,
            synopsis = synopsis,
            isbn = isbn,
            collections = collections,
            topics = topics,
            bookCopiesUrl = bookCopiesUrl
        )
    }

    /**
     * Gets an element from a key
     * @param key Key to search
     * @param doc Document
     * @return Element?
     */
    private fun getElement(key: String, doc: Document): Element? {
        return doc.select("td.bibInfoLabel").firstOrNull { it.text() == key }?.nextElementSibling()
    }

    /**
     * Gets multiple elements from a key. It is used for collections and topics
     * @param key Key to search
     * @param doc Document
     * @return List of elements
     */
    private fun getMultipleElements(key:String, doc: Document): MutableList<Element> {
        Log.d("BookConverterFactory", "constructBookDetails: Get collections")
        val collectionsElements = mutableListOf<Element>()

        var keyElement =
            doc.select("td.bibInfoLabel").firstOrNull { it.text() == key }

        if (keyElement == null) {
            return collectionsElements
        }

        do {
            val value = keyElement!!.nextElementSibling()?.select("a")
            val nextParent = keyElement.parent()?.nextElementSibling()
            keyElement = nextParent?.selectFirst("td")
            collectionsElements.addAll(value ?: emptyList())

        } while (keyElement != null && keyElement.text() == "")
        return collectionsElements
    }

    /**
     * Gets the number of results from a search
     * @param doc
     *
     * @return Number of results for a search
     */
    private fun getTotalSearchResults(doc: Document): Int {
        val divElement = doc.selectFirst("td.browseHeaderData") ?: return -1

        val text = divElement.text().replace(")", "")

        if (!text.contains(" de ")) {
            return -1
        }
        val total = text.split(" de ").last().toInt()

        return total
    }

    private fun constructBookCopies(element: Element): List<BookCopy> {
        Log.d("BookConverterFactory", "constructBookCopies")
        val trElements = element.select("tr.bibItemsEntry")
        val bookCopies = arrayListOf<BookCopy>()
        for (x in trElements) {
            val tdElements = x.select("td")
            val location = tdElements[0].text()
            val bibliotecaVirtualUrl = tdElements[0].selectFirst("a")?.attr("href")
                ?.replace(Regex("^https?://bibliotecavirtual\\.diba\\.cat/"), "")?.removeSuffix("?")

            if (location.isNotEmpty()) {
                val signature = tdElements[1].text()
                val status = tdElements[2].text()
                var notes: String? = tdElements[3].text()
                if (notes!!.isEmpty()) {
                    notes = null
                }

                val (bookCopyAvailability, statusColor) = when {
                    status.contains(
                        "Disponible",
                        ignoreCase = true
                    ) -> BookCopyAvailability.AVAILABLE to StatusColor.GREEN

                    status.contains(
                        "Prestatge reserva",
                        ignoreCase = true
                    ) -> BookCopyAvailability.CAN_RESERVE to StatusColor.YELLOW

                    status.contains(
                        "Venç el",
                        ignoreCase = true
                    ) -> BookCopyAvailability.CAN_RESERVE to StatusColor.YELLOW

                    status.contains(
                        "En trànsit",
                        ignoreCase = true
                    ) -> BookCopyAvailability.CAN_RESERVE to StatusColor.YELLOW

                    status.contains(
                        "IR PENDENT",
                        ignoreCase = true
                    ) -> BookCopyAvailability.CAN_RESERVE to StatusColor.YELLOW

                    else -> BookCopyAvailability.NOT_AVAILABLE to StatusColor.RED
                }

                bookCopies.add(
                    BookCopy(
                        location = location,
                        signature = signature,
                        status = status,
                        notes = notes,
                        availability = bookCopyAvailability,
                        statusColor = statusColor,
                        bibliotecaVirtualUrl = bibliotecaVirtualUrl,
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
                        image = image.split("&log=").first(),
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