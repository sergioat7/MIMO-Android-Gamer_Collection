package es.upsa.mimo.gamercollection.repositories

import android.content.Context
import es.upsa.mimo.gamercollection.models.FormatResponse
import es.upsa.mimo.gamercollection.persistence.AppDatabase
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

class FormatRepository @Inject constructor(
    private val database: AppDatabase
) {

    fun getFormats(): List<FormatResponse> {

        var formats = mutableListOf<FormatResponse>()
        runBlocking {
            val result = GlobalScope.async { database.formatDao().getFormats() }
            formats = result.await().toMutableList()
            formats.sortBy { it.name }
            val other = formats.firstOrNull { it.id == "OTHER" }
            formats.remove(other)
            other?.let {
                formats.add(it)
            }
        }
        return formats
    }

    fun insertFormat(format: FormatResponse) {

        GlobalScope.launch {
            database.formatDao().insertFormat(format)
        }
    }

    private fun deleteFormat(format: FormatResponse) {

        GlobalScope.launch {
            database.formatDao().deleteFormat(format)
        }
    }

    fun removeDisableContent(newFormats: List<FormatResponse>) {

        val currentFormats = getFormats()
        val formats = AppDatabase.getDisabledContent(currentFormats, newFormats) as List<*>
        for (format in formats) {
            deleteFormat(format as FormatResponse)
        }
    }

    fun manageFormats(formats: List<FormatResponse>) {

        for (format in formats) {
            insertFormat(format)
        }
        removeDisableContent(formats)
    }
}