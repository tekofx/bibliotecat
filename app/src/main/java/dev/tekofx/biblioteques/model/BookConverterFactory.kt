package dev.tekofx.biblioteques.model

import android.util.Log
import com.fleeksoft.ksoup.Ksoup
import com.fleeksoft.ksoup.nodes.Document
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

            val tdElements: Elements = doc.select("td.briefCitRow")

            println(tdElements.size)

            for (x in tdElements) {
                val descriptionElement = x.selectFirst("div.descript")
                val titleElement = descriptionElement?.selectFirst("span.titular")?.selectFirst("a")
                val imageElement = x.selectFirst("div.brief_portada")?.selectFirst("img")
                if (titleElement != null && imageElement != null) {
                    bookList.add(
                        Book(
                            id = "1",
                            title = titleElement.text(),
                            author = "test",
                            image = imageElement.attr("src")
                        )
                    )
                }
            }

            val response = BookResponse(bookList)
            response
        }
    }

    companion object {
        fun create(): BookConverterFactory {
            return BookConverterFactory()
        }
    }
}