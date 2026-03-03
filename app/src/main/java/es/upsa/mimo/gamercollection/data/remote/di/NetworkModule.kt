package es.upsa.mimo.gamercollection.data.remote.di

import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import es.upsa.mimo.gamercollection.BuildConfig
import es.upsa.mimo.gamercollection.data.remote.interfaces.RawgGameApiService
import java.util.concurrent.TimeUnit
import javax.inject.Singleton
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit

private const val BASE_ENDPOINT_RAWG = "https://api.rawg.io/api/"

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Singleton
    @Provides
    fun providesHttpLoggingInterceptor(): HttpLoggingInterceptor {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level =
            if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.HEADERS
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        return interceptor
    }

    @Singleton
    @Provides
    fun providesClientBuilder(logInterceptor: HttpLoggingInterceptor): OkHttpClient.Builder =
        OkHttpClient
            .Builder()
            .addInterceptor(logInterceptor)
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)

    @Singleton
    @Provides
    fun providesConverterFactory(): retrofit2.Converter.Factory {
        val json = Json { ignoreUnknownKeys = true }
        return json.asConverterFactory("application/json".toMediaType())
    }

    @Singleton
    @Provides
    fun providesRetrofit(
        clientBuilder: OkHttpClient.Builder,
        converterFactory: retrofit2.Converter.Factory,
    ): Retrofit = Retrofit
        .Builder()
        .baseUrl(BASE_ENDPOINT_RAWG)
        .client(clientBuilder.build())
        .addConverterFactory(converterFactory)
        .build()

    @Singleton
    @Provides
    fun providesRawgGameApiService(retrofit: Retrofit): RawgGameApiService =
        retrofit.create(RawgGameApiService::class.java)

    @Singleton
    @Provides
    fun providesFirebaseRemoteConfig(): FirebaseRemoteConfig {
        val remoteConfig = Firebase.remoteConfig
        remoteConfig.setConfigSettingsAsync(
            remoteConfigSettings {
                minimumFetchIntervalInSeconds = 3600
            },
        )
        return remoteConfig
    }
}