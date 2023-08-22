package es.upsa.mimo.gamercollection.injection.modules

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import es.upsa.mimo.gamercollection.persistence.AppDatabase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppDatabaseModule {

    @Singleton
    @Provides
    fun provideAppDatabase(@ApplicationContext context: Context) = AppDatabase.getAppDatabase(context)
}