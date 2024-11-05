package dev.tekofx.biblioteques.model.book

import com.fleeksoft.ksoup.nodes.Element
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
            val response = BookResponse(responseBody.string())
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