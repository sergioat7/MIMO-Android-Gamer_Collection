package es.upsa.mimo.gamercollection.persistence.repositories

import android.content.Context
import es.upsa.mimo.gamercollection.models.FormatResponse
import es.upsa.mimo.gamercollection.persistence.AppDatabase
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class FormatRepository(context: Context) {

    private val database = AppDatabase.getAppDatabase(context)
    private val formatDao = database.formatDao()

    fun getFormats(): List<FormatResponse> {

        var formats: List<FormatResponse> = arrayListOf()
        runBlocking {
            val result = GlobalScope.async { formatDao.getFormats() }
            formats = result.await()
        }
        return formats.sortedBy { it.name }
    }

    fun insertFormat(format: FormatResponse) {

        GlobalScope.launch {
            formatDao.insertFormat(format)
        }
    }

    private fun deleteFormat(format: FormatResponse) {

        GlobalScope.launch {
            formatDao.deleteFormat(format)
        }
    }

    fun removeDisableContent(newFormats: List<FormatResponse>) {

        val currentFormats = getFormats()
        val formats = AppDatabase.getDisabledContent(currentFormats, newFormats) as List<FormatResponse>
        for (format in formats) {
            deleteFormat(format)
        }
    }
}