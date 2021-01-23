package es.upsa.mimo.gamercollection.injection.modules

import android.content.Context
import dagger.Module
import dagger.Provides
import es.upsa.mimo.gamercollection.persistence.AppDatabase

@Module
class AppDatabaseModule (private val context: Context) {

    @Provides
    fun provideAppDatabase() = AppDatabase.getAppDatabase(context)
}