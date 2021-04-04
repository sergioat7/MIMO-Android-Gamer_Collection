package es.upsa.mimo.gamercollection.repositories

import es.upsa.mimo.gamercollection.models.responses.ErrorResponse
import es.upsa.mimo.gamercollection.models.responses.FormatResponse
import es.upsa.mimo.gamercollection.network.apiClient.FormatAPIClient
import es.upsa.mimo.gamercollection.persistence.AppDatabase
import es.upsa.mimo.gamercollection.utils.Constants
import kotlinx.coroutines.*
import javax.inject.Inject

class FormatRepository @Inject constructor(
    private val database: AppDatabase
) {

    //region Private properties
    private val formatAPIClient = FormatAPIClient()
    private val databaseScope = CoroutineScope(Job() + Dispatchers.IO)
    //endregion

    //region Public methods
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

            val result = databaseScope.async {
                database.formatDao().getFormats()
            }
            formats = result.await().toMutableList()
            formats.sortBy { it.name }
            val other = formats.firstOrNull { it.id == Constants.OTHER_VALUE }
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
    //endregion

    //region Private methods
    private fun insertFormatDatabase(format: FormatResponse) {

        runBlocking {
            val job = databaseScope.launch {
                database.formatDao().insertFormat(format)
            }
            job.join()
        }
    }

    private fun deleteFormatDatabase(format: FormatResponse) {

        runBlocking {
            val job = databaseScope.launch {
                database.formatDao().deleteFormat(format)
            }
            job.join()
        }
    }
    //endregion
}