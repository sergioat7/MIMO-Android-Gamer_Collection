package es.upsa.mimo.gamercollection.network

import android.content.res.Resources
import com.google.gson.Gson
import es.upsa.mimo.gamercollection.R
import es.upsa.mimo.gamercollection.models.ErrorResponse
import es.upsa.mimo.gamercollection.models.EmptyResponse
import es.upsa.mimo.gamercollection.utils.Constants
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class APIClient(resources: Resources) {
    companion object {

        val gson = Gson()

        fun getRetrofit(): Retrofit {
            return Retrofit.Builder().baseUrl(Constants.baseEndpoint).addConverterFactory(GsonConverterFactory.create()).build()
        }

        inline fun <reified T, reified U> sendServer(resources: Resources, request: Call<T>, crossinline success: (T) -> Unit, crossinline failure: (U) -> Unit) {

            request.enqueue(object : Callback<T> {
                override fun onResponse(call: Call<T>?, response: Response<T>) {

                    if ( response.isSuccessful && (response.code() == 204 || T::class.java == EmptyResponse::javaClass) ) {

                        val objectData = gson.toJson("{}")
                        val arrayData = gson.toJson("[]")

                        var result = gson.fromJson(objectData, T::class.java)
                        if (result != null) {
                            success(result)
                            return
                        }

                        result = gson.fromJson(arrayData, T::class.java)
                        if (result != null) {
                            success(result)
                            return
                        }

                        val error = ErrorResponse(Resources.getSystem().getString(R.string.ERROR_SERVER)) as U
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
                        val error = ErrorResponse(Resources.getSystem().getString(R.string.ERROR_SERVER)) as U
                        failure(error)
                    }
                }

                override fun onFailure(call: Call<T>?, t: Throwable) {

                    val error = ErrorResponse(resources.getString(R.string.ERROR_SERVER)) as U
                    failure(error)
                }
            })
        }
    }
}