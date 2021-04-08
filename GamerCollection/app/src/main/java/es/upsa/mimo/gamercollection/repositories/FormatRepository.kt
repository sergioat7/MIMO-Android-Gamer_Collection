package es.upsa.mimo.gamercollection.repositories

import es.upsa.mimo.gamercollection.models.responses.ErrorResponse
import es.upsa.mimo.gamercollection.models.responses.FormatResponse
import es.upsa.mimo.gamercollection.network.ApiManager
import es.upsa.mimo.gamercollection.network.FormatApiService
import es.upsa.mimo.gamercollection.network.RequestResult
import es.upsa.mimo.gamercollection.persistence.AppDatabase
import kotlinx.coroutines.*
import javax.inject.Inject

class FormatRepository @Inject constructor(
    private val database: AppDatabase
) {

    //region Private properties
    private val api = ApiManager.getService<FormatApiService>(ApiManager.BASE_ENDPOINT)
    private val databaseScope =
        CoroutineScope(Job() + Dispatchers.IO)//TODO: inject scope (see best practices for coroutines)
    //endregion

    //region Public methods
    fun loadFormats(success: () -> Unit, failure: (ErrorResponse) -> Unit) {

        databaseScope.async {//TODO: change scope
            when (val response = ApiManager.validateResponse(api.getFormats())) {
                is RequestResult.JsonSuccess -> {

                    val newFormats = response.body ?: listOf()
                    for (newFormat in newFormats) {
                        insertFormatDatabase(newFormat)
                    }
                    val currentFormats = getFormatsDatabase()
                    val formatsToRemove = AppDatabase.getDisabledContent(currentFormats, newFormats)
                    for (format in formatsToRemove) {
                        deleteFormatDatabase(format as FormatResponse)
                    }
                    success()
                }
                is RequestResult.Failure -> failure(response.error)
            }
        }
    }

    fun getFormatsDatabase(): List<FormatResponse> {

        var formats = mutableListOf<FormatResponse>()
        runBlocking {

            val result = databaseScope.async {
                database.formatDao().getFormats()
            }
            formats = result.await().toMutableList()
            formats.sortBy { it.name }
            val other = formats.firstOrNull { it.id == ApiManager.OTHER_VALUE }
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