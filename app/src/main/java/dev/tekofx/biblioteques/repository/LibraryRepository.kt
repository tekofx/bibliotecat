package dev.tekofx.biblioteques.repository

import dev.tekofx.biblioteques.call.LibraryService

class LibraryRepository(
    private val libraryService: LibraryService
) {
    fun getLibraries() = libraryService.getLibraries()
    fun getLibrary(pointId: String) = libraryService.getLibrary(pointId)
}