package es.upsa.mimo.gamercollection.injection.modules

import dagger.Module
import dagger.Provides
import es.upsa.mimo.gamercollection.network.*
import javax.inject.Qualifier

@Module
class NetworkModule {
    @Provides
    fun providesFormatApiService(): FormatApiService = ApiManager.getService(
        ApiManager.BASE_ENDPOINT
    )

    @Provides
    fun providesGameApiService(): GameApiService = ApiManager.getService(
        ApiManager.BASE_ENDPOINT
    )

    @Provides
    fun providesGenreApiService(): GenreApiService = ApiManager.getService(
        ApiManager.BASE_ENDPOINT
    )

    @Provides
    fun providesPlatformApiService(): PlatformApiService = ApiManager.getService(
        ApiManager.BASE_ENDPOINT
    )

    @Provides
    fun providesRawgGameApiService(): RawgGameApiService = ApiManager.getService(
        ApiManager.BASE_ENDPOINT
    )

    @Provides
    fun providesSagaApiService(): SagaApiService = ApiManager.getService(
        ApiManager.BASE_ENDPOINT
    )

    @Provides
    fun providesSongApiService(): SongApiService = ApiManager.getService(
        ApiManager.BASE_ENDPOINT
    )

    @Provides
    fun providesStateApiService(): StateApiService = ApiManager.getService(
        ApiManager.BASE_ENDPOINT
    )

    @Provides
    fun providesUserApiService(): UserApiService = ApiManager.getService(
        ApiManager.BASE_ENDPOINT
    )
}