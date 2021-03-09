package es.upsa.mimo.gamercollection.network.apiClient

import com.google.gson.*
import es.upsa.mimo.gamercollection.R
import es.upsa.mimo.gamercollection.models.responses.ErrorResponse
import es.upsa.mimo.gamercollection.utils.Constants
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.reflect.Type
import java.util.*
import java.util.concurrent.TimeUnit

class APIClient {
    companion object {

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

        private val okHttpClient: OkHttpClient = OkHttpClient.Builder()
            .connectTimeout(1, TimeUnit.MINUTES)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .build()

        val retrofit: Retrofit =
            Retrofit
                .Builder()
                .baseUrl(Constants.BASE_ENDPOINT)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()

        inline fun <reified T, reified U> sendServer(request: Call<T>, crossinline success: (T) -> Unit, crossinline failure: (U) -> Unit) {

            request.enqueue(object : Callback<T> {
                override fun onResponse(call: Call<T>?, response: Response<T>) {

                    val isSuccessful = response.isSuccessful
                    val code = response.code()
                    val body = response.body()
                    val errorBody = response.errorBody()

                    if ( isSuccessful && (code == 204 || T::class == Void::class) ) {

                        if (T::class == Void::class) {
                            val result = gson.fromJson(Constants.EMPTY_VALUE, T::class.java)
                            success(result)
                            return
                        }

                        try {
                            val result = gson.fromJson("{}", T::class.java)
                            success(result)
                            return
                        } catch (e: Exception){}

                        try {
                            val result = gson.fromJson("[]", T::class.java)
                            success(result)
                            return
                        } catch (e: Exception){}

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
    }
}