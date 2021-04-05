package es.upsa.mimo.gamercollection.network.apiClient

import com.google.gson.*
import es.upsa.mimo.gamercollection.BuildConfig
import es.upsa.mimo.gamercollection.R
import es.upsa.mimo.gamercollection.models.responses.ErrorResponse
import es.upsa.mimo.gamercollection.utils.Constants
import es.upsa.mimo.gamercollection.utils.SharedPreferencesHelper
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
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
                    Constants.stringToDate(json.asString)
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
        return retrofits[T::class] as Retrofit? ?: {

            val logInterceptor = HttpLoggingInterceptor()
            logInterceptor.level =
                if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.HEADERS
                else HttpLoggingInterceptor.Level.NONE

            val clientBuilder =
                OkHttpClient.Builder()
                    .addInterceptor(logInterceptor)
                    .addInterceptor(TokenInterceptor())
                    .connectTimeout(1, TimeUnit.MINUTES)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(15, TimeUnit.SECONDS)

            val retrofit =
                Retrofit.Builder()
                    .baseUrl(url)
                    .client(clientBuilder.build())
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build()

            this.retrofits[T::class] = retrofit
            retrofit
        }()
    }

    inline fun <reified T : Any> getService(url: String): T {
        return apis[T::class] as? T ?: {
            val ret = getRetrofit<T>(url).create(T::class.java)
            apis[T::class] = ret
            ret
        }()
    }

    inline fun <reified T, reified U> sendServer(
        request: Call<T>,
        crossinline success: (T) -> Unit,
        crossinline failure: (U) -> Unit
    ) {

        request.enqueue(object : Callback<T> {
            override fun onResponse(call: Call<T>?, response: retrofit2.Response<T>) {

                val isSuccessful = response.isSuccessful
                val code = response.code()
                val body = response.body()
                val errorBody = response.errorBody()

                if (isSuccessful && (code == 204 || T::class == Void::class)) {

                    if (T::class == Void::class) {
                        val result = gson.fromJson(Constants.EMPTY_VALUE, T::class.java)
                        success(result)
                        return
                    }

                    try {
                        val result = gson.fromJson("{}", T::class.java)
                        success(result)
                        return
                    } catch (e: Exception) {
                    }

                    try {
                        val result = gson.fromJson("[]", T::class.java)
                        success(result)
                        return
                    } catch (e: Exception) {
                    }

                    val error = ErrorResponse(Constants.EMPTY_VALUE, R.string.error_server) as U
                    failure(error)
                } else if (isSuccessful && body != null) {

                    val result = body as T
                    success(result)
                } else if (!isSuccessful && errorBody != null) {

                    val errorResponse: U = gson.fromJson(
                        errorBody.charStream(), U::class.java
                    )
                    failure(errorResponse)
                } else {

                    val error = ErrorResponse(Constants.EMPTY_VALUE, R.string.error_server) as U
                    failure(error)
                }
            }

            override fun onFailure(call: Call<T>?, t: Throwable) {

                val error = ErrorResponse(Constants.EMPTY_VALUE, R.string.error_server) as U
                failure(error)
            }
        })
    }
    //endregion

    //region TokenInterceptor
    class TokenInterceptor : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {

            val original = chain.request()

            val request = original.newBuilder()
                .addHeader(ACCEPT_LANGUAGE_HEADER, SharedPreferencesHelper.getLanguage())
                .build()

            return chain.proceed(request)
        }
    }
    //endregion
}