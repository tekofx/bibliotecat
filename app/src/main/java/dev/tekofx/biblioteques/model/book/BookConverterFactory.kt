package dev.tekofx.biblioteques.model.book

import android.util.Log
import com.fleeksoft.ksoup.Ksoup
import com.fleeksoft.ksoup.nodes.Document
import com.fleeksoft.ksoup.nodes.Element
import com.fleeksoft.ksoup.select.Elements
import dev.tekofx.biblioteques.dto.BookResponse
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.reflect.Type

class BookConverterFactory : Converter.Factory() {

    override fun responseBodyConverter(
        type: Type, annotations: Array<Annotation>, retrofit: Retrofit
    ): Converter<ResponseBody, BookResponse> {
        return Converter { responseBody ->
            val bookList = arrayListOf<Book>()
            Log.d("BookConverterFactory", "1")
            val doc: Document = Ksoup.parse(html = responseBody.string())
            Log.d("BookConverterFactory", "2")

            val bookElements: Elements = doc.select("td.briefCitRow")

            for (bookElement in bookElements) {
                val descriptionElement = bookElement.selectFirst("div.descript")
                val titleElement = descriptionElement?.selectFirst("span.titular")?.selectFirst("a")
                val imageElement = bookElement.selectFirst("div.brief_portada")?.selectFirst("img")

                val descriptionFields = descriptionElement.toString().split("<br>")


                val author = descriptionFields[2].trim()
                val edition = descriptionFields[3].split("<!--")[0].trim()

                //val bookCopies = getBookCopies(bookElement)

                if (titleElement != null && imageElement != null) {
                    bookList.add(
                        Book(
                            id = "1",
                            title = titleElement.text(),
                            author = author,
                            image = imageElement.attr("src"),
                            edition = edition
                        )
                    )
                }
            }

            val response = BookResponse(bookList)
            response
        }
    }

    private fun getBookCopies(element: Element): List<BookCopy> {
        val trElements = element.select("tr.bibItemsEntry")
        val bookCopies = arrayListOf<BookCopy>()
        for (x in trElements) {
            val tdElements = x.select("td")
            val location = tdElements[0].text()
            if (location.isEmpty()) {
                val signature = tdElements[1].text()
                val status = tdElements[2].text()
                val notes = tdElements[3].text()
                bookCopies.add(
                    BookCopy(
                        location = location,
                        signature = signature,
                        status = status,
                        notes = notes
                    )
                )


            }
        }

        return bookCopies


    }

    companion object {
        fun create(): BookConverterFactory {
            return BookConverterFactory()
        }
    }
}