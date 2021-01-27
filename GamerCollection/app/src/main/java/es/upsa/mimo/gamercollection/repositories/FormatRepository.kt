package es.upsa.mimo.gamercollection.repositories

import es.upsa.mimo.gamercollection.models.FormatResponse
import es.upsa.mimo.gamercollection.persistence.AppDatabase
import es.upsa.mimo.gamercollection.utils.Constants
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

class FormatRepository @Inject constructor(
    private val database: AppDatabase
) {

    // MARK: - Public methods

    fun getFormats(): List<FormatResponse> {

        var formats = mutableListOf<FormatResponse>()
        runBlocking {
            val result = GlobalScope.async { database.formatDao().getFormats() }
            formats = result.await().toMutableList()
            formats.sortBy { it.name }
            val other = formats.firstOrNull { it.id == Constants.DEFAULT_PLATFORM }
            formats.remove(other)
            other?.let {
                formats.add(it)
            }
        }
        return formats
    }

    private fun deleteFormat(format: FormatResponse) {

        GlobalScope.launch {
            database.formatDao().deleteFormat(format)
        }
    }

    fun manageFormats(newFormats: List<FormatResponse>) {

        for (newFormat in newFormats) {
            insertFormat(newFormat)
        }

        val currentFormats = getFormats()
        val formatsToRemove = AppDatabase.getDisabledContent(currentFormats, newFormats)
        for (format in formatsToRemove) {
            deleteFormat(format as FormatResponse)
        }
    }

    // MARK: - Private methods

    private fun insertFormat(format: FormatResponse) {

        GlobalScope.launch {
            database.formatDao().insertFormat(format)
        }
    }
}