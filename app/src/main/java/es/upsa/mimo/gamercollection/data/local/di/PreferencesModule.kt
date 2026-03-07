package es.upsa.mimo.gamercollection.data.local.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import es.upsa.mimo.gamercollection.data.local.SharedPreferencesHelper
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PreferencesModule {

    @Singleton
    @Provides
    fun provideSharedPreferencesHelper(
        @ApplicationContext context: Context,
    ): SharedPreferencesHelper = SharedPreferencesHelper(context)
}