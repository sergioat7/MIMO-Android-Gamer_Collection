package es.upsa.mimo.gamercollection.repositories

import es.upsa.mimo.gamercollection.models.responses.ErrorResponse
import es.upsa.mimo.gamercollection.models.responses.FormatResponse
import es.upsa.mimo.gamercollection.network.apiClient.FormatAPIClient
import es.upsa.mimo.gamercollection.persistence.AppDatabase
import es.upsa.mimo.gamercollection.utils.Constants
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

class FormatRepository @Inject constructor(
    private val database: AppDatabase,
    private val formatAPIClient: FormatAPIClient
) {

    // MARK: - Public methods

    fun loadFormats(success: () -> Unit, failure: (ErrorResponse) -> Unit) {

        formatAPIClient.getFormats({ newFormats ->

            for (newFormat in newFormats) {
                insertFormatDatabase(newFormat)
            }
            val currentFormats = getFormatsDatabase()
            val formatsToRemove = AppDatabase.getDisabledContent(currentFormats, newFormats)
            for (format in formatsToRemove) {
                deleteFormatDatabase(format as FormatResponse)
            }
            success()
        }, failure)
    }

    fun getFormatsDatabase(): List<FormatResponse> {

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

    fun resetTable() {

        val formats = getFormatsDatabase()
        for (format in formats) {
            deleteFormatDatabase(format)
        }
    }

    // MARK: - Private methods

    private fun insertFormatDatabase(format: FormatResponse) {

        GlobalScope.launch {
            database.formatDao().insertFormat(format)
        }
    }

    private fun deleteFormatDatabase(format: FormatResponse) {

        GlobalScope.launch {
            database.formatDao().deleteFormat(format)
        }
    }
}