package es.upsa.mimo.gamercollection.persistence.repositories

import android.content.Context
import es.upsa.mimo.gamercollection.models.GenreResponse
import es.upsa.mimo.gamercollection.persistence.AppDatabase
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class GenreRepository(context: Context) {

    private val database = AppDatabase.getAppDatabase(context)
    private val genreDao = database.genreDao()

    fun getGenres(): List<GenreResponse> {

        var genres = mutableListOf<GenreResponse>()
        runBlocking {
            val result = GlobalScope.async { genreDao.getGenres() }
            genres = result.await().toMutableList()
            genres.sortBy { it.name }
            val other = genres.firstOrNull { it.id == "OTHER" }
            genres.remove(other)
            other?.let {
                genres.add(it)
            }
        }
        return genres
    }

    fun insertGenre(genre: GenreResponse) {

        GlobalScope.launch {
            genreDao.insertGenre(genre)
        }
    }

    private fun deleteGenre(genre: GenreResponse) {

        GlobalScope.launch {
            genreDao.deleteGenre(genre)
        }
    }

    fun removeDisableContent(newGenres: List<GenreResponse>) {

        val currentGenres = getGenres()
        val genres = AppDatabase.getDisabledContent(currentGenres, newGenres) as List<*>
        for (genre in genres) {
            deleteGenre(genre as GenreResponse)
        }
    }
}