package es.upsa.mimo.gamercollection.network.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import es.upsa.mimo.gamercollection.network.*
import es.upsa.mimo.gamercollection.network.interfaces.GameApiService
import es.upsa.mimo.gamercollection.network.interfaces.RawgGameApiService
import es.upsa.mimo.gamercollection.network.interfaces.SagaApiService
import es.upsa.mimo.gamercollection.network.interfaces.SongApiService
import es.upsa.mimo.gamercollection.network.interfaces.UserApiService
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Singleton
    @Provides
    fun providesGameApiService(): GameApiService = ApiManager.getService(
        ApiManager.BASE_ENDPOINT
    )

    @Singleton
    @Provides
    fun providesRawgGameApiService(): RawgGameApiService = ApiManager.getService(
        ApiManager.BASE_ENDPOINT_RAWG
    )

    @Singleton
    @Provides
    fun providesSagaApiService(): SagaApiService = ApiManager.getService(
        ApiManager.BASE_ENDPOINT
    )

    @Singleton
    @Provides
    fun providesSongApiService(): SongApiService = ApiManager.getService(
        ApiManager.BASE_ENDPOINT
    )

    @Singleton
    @Provides
    fun providesUserApiService(): UserApiService = ApiManager.getService(
        ApiManager.BASE_ENDPOINT
    )
}