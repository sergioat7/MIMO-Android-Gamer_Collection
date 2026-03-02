package es.upsa.mimo.gamercollection.data.remote.di

import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import es.upsa.mimo.gamercollection.BuildConfig
import es.upsa.mimo.gamercollection.data.remote.interfaces.RawgGameApiService
import es.upsa.mimo.gamercollection.extensions.toDate
import es.upsa.mimo.gamercollection.utils.Constants
import java.lang.reflect.Type
import java.util.Date
import java.util.concurrent.TimeUnit
import javax.inject.Singleton
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private const val BASE_ENDPOINT_RAWG = "https://api.rawg.io/api/"

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Singleton
    @Provides
    fun providesGson(): Gson = GsonBuilder()
        .registerTypeAdapter(
            Date::class.java,
            JsonDeserializer<Date> { json: JsonElement, _: Type?, _: JsonDeserializationContext? ->
                json.asString.toDate()
            },
        ).setDateFormat(Constants.DATE_FORMAT)
        .serializeNulls()
        .create()

    @Singleton
    @Provides
    fun providesRetrofit(gson: Gson): Retrofit {
        val logInterceptor = HttpLoggingInterceptor()
        logInterceptor.level =
            if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.HEADERS
            } else {
                HttpLoggingInterceptor.Level.NONE
            }

        val clientBuilder =
            OkHttpClient
                .Builder()
                .addInterceptor(logInterceptor)
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)

        val retrofit =
            Retrofit
                .Builder()
                .baseUrl(BASE_ENDPOINT_RAWG)
                .client(clientBuilder.build())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()

        return retrofit
    }

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