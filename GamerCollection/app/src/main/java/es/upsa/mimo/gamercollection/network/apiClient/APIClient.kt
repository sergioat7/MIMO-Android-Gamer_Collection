package es.upsa.mimo.gamercollection.network.apiClient

import android.content.res.Resources
import com.google.gson.*
import es.upsa.mimo.gamercollection.R
import es.upsa.mimo.gamercollection.models.ErrorResponse
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

        var gson = GsonBuilder()
            .registerTypeAdapter(Date::class.java, object : JsonDeserializer<Date?> {
                @Throws(JsonParseException::class)
                override fun deserialize(json: JsonElement, typeOfT: Type?, context: JsonDeserializationContext?): Date? {
                    return Constants.stringToDate(json.asString)
                }
            })
            .setDateFormat(Constants.getDateFormat())
            .serializeNulls()
            .create()



        private val okHttpClient: OkHttpClient = OkHttpClient.Builder()
            .connectTimeout(1, TimeUnit.MINUTES)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .build()

        fun getRetrofit(): Retrofit {

            return Retrofit.Builder()
                .baseUrl(Constants.baseEndpoint)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
        }

        inline fun <reified T, reified U> sendServer(resources: Resources, request: Call<T>, crossinline success: (T) -> Unit, crossinline failure: (U) -> Unit) {

            request.enqueue(object : Callback<T> {
                override fun onResponse(call: Call<T>?, response: Response<T>) {

                    if ( response.isSuccessful && response.code() == 204 ) {

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

                        val error = ErrorResponse(resources.getString(R.string.ERROR_SERVER)) as U
                        failure(error)
                    } else if (response.isSuccessful && response.body() != null) {

                        val result = response.body() as T
                        success(result)
                    } else if (!response.isSuccessful && response.errorBody() != null) {

                        val errorResponse: U = gson.fromJson(
                            response.errorBody()!!.charStream(), U::class.java
                        )
                        failure(errorResponse)
                    } else {

                        val error = ErrorResponse(resources.getString(R.string.ERROR_SERVER)) as U
                        failure(error)
                    }
                }

                override fun onFailure(call: Call<T>?, t: Throwable) {

                    val error = ErrorResponse(resources.getString(R.string.ERROR_SERVER)) as U
                    failure(error)
                }
            })
        }

        inline fun <reified U> sendServerWithVoidResponse(resources: Resources, request: Call<Void>, crossinline success: () -> Unit, crossinline failure: (U) -> Unit) {

            request.enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>?, response: Response<Void>) {

                    if ( response.isSuccessful ) {
                        success()
                    } else if (!response.isSuccessful && response.errorBody() != null) {

                        val errorResponse: U = gson.fromJson(
                            response.errorBody()!!.charStream(), U::class.java
                        )
                        failure(errorResponse)
                    } else {

                        val error = ErrorResponse(resources.getString(R.string.ERROR_SERVER)) as U
                        failure(error)
                    }
                }

                override fun onFailure(call: Call<Void>?, t: Throwable) {

                    val error = ErrorResponse(resources.getString(R.string.ERROR_SERVER)) as U
                    failure(error)
                }
            })
        }
    }
}