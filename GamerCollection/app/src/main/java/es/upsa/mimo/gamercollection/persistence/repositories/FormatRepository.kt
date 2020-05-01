package es.upsa.mimo.gamercollection.persistence.repositories

import android.content.Context
import es.upsa.mimo.gamercollection.models.FormatResponse
import es.upsa.mimo.gamercollection.persistence.AppDatabase

class FormatRepository(context: Context) {

    private val database = AppDatabase.getAppDatabase(context)
    private val formatDao = database.formatDao()

    fun getFormats(): List<FormatResponse> {
        return formatDao.getFormats()
    }

    fun insertFormat(format: FormatResponse) {
        formatDao.insertFormat(format)
    }
}