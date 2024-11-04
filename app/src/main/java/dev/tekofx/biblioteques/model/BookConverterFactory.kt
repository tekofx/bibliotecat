package dev.tekofx.biblioteques.model

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
            val list = arrayListOf(
                Book("1", "Title", "Author"),
                Book("2", "Title2", "Author2")

            )

            val response = BookResponse(list)
            response
        }
    }

    companion object {
        fun create(): BookConverterFactory {
            return BookConverterFactory()
        }
    }
}