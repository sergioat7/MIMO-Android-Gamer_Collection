package es.upsa.mimo.gamercollection.data.local.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import es.upsa.mimo.gamercollection.data.local.SharedPreferencesHelper
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PreferencesModule {

    @Singleton
    @Provides
    fun provideSharedPreferencesHelper(): SharedPreferencesHelper = SharedPreferencesHelper
}