package es.upsa.mimo.gamercollection.repositories

import es.upsa.mimo.gamercollection.R
import es.upsa.mimo.gamercollection.injection.modules.IoDispatcher
import es.upsa.mimo.gamercollection.injection.modules.MainDispatcher
import es.upsa.mimo.gamercollection.models.responses.ErrorResponse
import es.upsa.mimo.gamercollection.models.responses.FormatResponse
import es.upsa.mimo.gamercollection.network.ApiManager
import es.upsa.mimo.gamercollection.network.FormatApiService
import es.upsa.mimo.gamercollection.network.RequestResult
import es.upsa.mimo.gamercollection.persistence.AppDatabase
import es.upsa.mimo.gamercollection.utils.Constants
import kotlinx.coroutines.*
import javax.inject.Inject

class FormatRepository @Inject constructor(
    private val api: FormatApiService,
    private val database: AppDatabase,
    @MainDispatcher private val mainDispatcher: CoroutineDispatcher,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {

    //region Private properties
    private val externalScope = CoroutineScope(Job() + mainDispatcher)
    private val databaseScope = CoroutineScope(Job() + ioDispatcher)
    //endregion

    //region Public methods
    fun loadFormats(success: () -> Unit, failure: (ErrorResponse) -> Unit) {
        externalScope.launch {

            try {
                when (val response = ApiManager.validateResponse(api.getFormats())) {
                    is RequestResult.JsonSuccess -> {

                        val newFormats = response.body
                        for (newFormat in newFormats) {
                            insertFormatDatabase(newFormat)
                        }
                        val currentFormats = getFormatsDatabase()
                        val formatsToRemove =
                            AppDatabase.getDisabledContent(currentFormats, newFormats)
                        for (format in formatsToRemove) {
                            deleteFormatDatabase(format as FormatResponse)
                        }
                        success()
                    }
                    is RequestResult.Failure -> failure(response.error)
                    else -> failure(ErrorResponse(Constants.EMPTY_VALUE, R.string.error_server))
                }
            } catch (e: Exception) {
                failure(ErrorResponse(Constants.EMPTY_VALUE, R.string.error_server_connection))
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