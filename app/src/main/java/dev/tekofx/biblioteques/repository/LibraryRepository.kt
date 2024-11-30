package dev.tekofx.biblioteques.repository

import dev.tekofx.biblioteques.call.LibraryService

class LibraryRepository(
    private val libraryService: LibraryService
) {
    fun getLibraries() = libraryService.getLibraries()
}