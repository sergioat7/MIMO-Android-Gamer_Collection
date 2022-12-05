package es.upsa.mimo.gamercollection.network

import com.google.gson.*
import es.upsa.mimo.gamercollection.BuildConfig
import es.upsa.mimo.gamercollection.R
import es.upsa.mimo.gamercollection.extensions.toDate
import es.upsa.mimo.gamercollection.models.responses.ErrorResponse
import es.upsa.mimo.gamercollection.utils.Constants
import es.upsa.mimo.gamercollection.utils.SharedPreferencesHelper
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.reflect.Type
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.reflect.KClass

object ApiManager {

    //region Static properties
    const val BASE_ENDPOINT = "https://videogames-collection-services.herokuapp.com/"
    const val BASE_ENDPOINT_RAWG = "https://api.rawg.io/api/"
    const val ACCEPT_LANGUAGE_HEADER = "Accept-Language"
    const val AUTHORIZATION_HEADER = "Authorization"
    const val OTHER_VALUE = "OTHER"
    const val KEY_PARAM = "key"
    const val KEY_VALUE = "747a7639039d4134a4370852b0f6b282"
    const val PAGE_PARAM = "page"
    const val PAGE_SIZE_PARAM = "page_size"
    const val PAGE_SIZE = 20
    const val SEARCH_PARAM = "search"
    //endregion

    //region Public properties
    val gson: Gson =
        GsonBuilder()
            .registerTypeAdapter(
                Date::class.java,
                JsonDeserializer<Date> { json: JsonElement, _: Type?, _: JsonDeserializationContext? ->
                    json.asString.toDate()
                }
            )
            .setDateFormat(Constants.DATE_FORMAT)
            .serializeNulls()
            .create()

    var retrofits: MutableMap<KClass<*>, Any> = mutableMapOf()

    var apis: MutableMap<KClass<*>, Any> = mutableMapOf()
    //endregion

    //region Public methods
    inline fun <reified T : Any> getRetrofit(url: String): Retrofit {
        return retrofits[T::class] as Retrofit? ?: run {

            val logInterceptor = HttpLoggingInterceptor()
            logInterceptor.level =
                if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.HEADERS
                else HttpLoggingInterceptor.Level.NONE

            val clientBuilder =
                OkHttpClient.Builder()
                    .addInterceptor(logInterceptor)
                    .addInterceptor(TokenInterceptor())
                    .connectTimeout(2, TimeUnit.MINUTES)
                    .readTimeout(60, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)

            val retrofit =
                Retrofit.Builder()
                    .baseUrl(url)
                    .client(clientBuilder.build())
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build()

            retrofits[T::class] = retrofit
            retrofit
        }
    }

    inline fun <reified T : Any> getService(url: String): T {
        return apis[T::class] as? T ?: run {

            val ret = getRetrofit<T>(url).create(T::class.java)
            apis[T::class] = ret
            ret
        }
    }

    inline fun <reified T : Any> validateResponse(response: retrofit2.Response<T>): RequestResult<T> {

        val isSuccessful = response.isSuccessful
        val code = response.code()
        val body = response.body()
        val error = response.errorBody()
        return when {
            isSuccessful -> {
                when {
                    T::class == Unit::class -> RequestResult.Success
                    body != null && code != 204 -> RequestResult.JsonSuccess(body)
                    else -> getEmptyResponse() ?: RequestResult.Failure(
                        ErrorResponse(Constants.EMPTY_VALUE, R.string.error_server)
                    )
                }
            }
            code < 500 && error != null -> {
                RequestResult.Failure(
                    gson.fromJson(
                        error.charStream(),
                        ErrorResponse::class.java
                    )
                )
            }
            else -> {
                RequestResult.Failure(
                    ErrorResponse(
                        Constants.EMPTY_VALUE,
                        R.string.error_server
                    )
                )
            }
        }
    }

    inline fun <reified T : Any> getEmptyResponse(): RequestResult<T>? {

        try {
            return RequestResult.JsonSuccess(gson.fromJson("{}", T::class.java))
        } catch (e: Exception) {
        }

        try {
            return RequestResult.JsonSuccess(gson.fromJson("[]", T::class.java))
        } catch (e: Exception) {
        }

        return null
    }
    //endregion

    //region TokenInterceptor
    class TokenInterceptor : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {

            val authRequirement = chain.request().header(AUTHORIZATION_HEADER)
            val original = chain.request()

            val request = if (authRequirement != null) {

                val accessToken = SharedPreferencesHelper.credentials.token
                original.newBuilder()
                    .addHeader(ACCEPT_LANGUAGE_HEADER, SharedPreferencesHelper.language)
                    .header(AUTHORIZATION_HEADER, accessToken)
                    .build()
            } else {
                original.newBuilder()
                    .addHeader(ACCEPT_LANGUAGE_HEADER, SharedPreferencesHelper.language)
                    .build()
            }

            return chain.proceed(request)
        }
    }
    //endregion
}