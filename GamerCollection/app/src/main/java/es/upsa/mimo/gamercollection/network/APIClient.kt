package es.upsa.mimo.gamercollection.network

import android.content.res.Resources
import com.google.gson.Gson
import es.upsa.mimo.gamercollection.R
import es.upsa.mimo.gamercollection.models.ErrorResponse
import es.upsa.mimo.gamercollection.models.LoginResponse
import retrofit2.Call

class APIClient {
    companion object {

        val gson = Gson()

        inline fun <reified T, reified U> sendServer(request: Call<T>, success: (T) -> Unit, failure: (U) -> Unit) {

            val call = request.execute()

            val statusCode = call.code()

            //TODO change LoginResponse to EmptyResponse
            if ( statusCode < 400 && (statusCode == 204 || T::class.java == LoginResponse::javaClass) ) {

                val objectData = gson.toJson("{}")
                val arrayData = gson.toJson("[]")

                var response = gson.fromJson(objectData, T::class.java)
                if (response != null) {
                    success(response)
                    return
                }

                response = gson.fromJson(arrayData, T::class.java)
                if (response != null) {
                    success(response)
                    return
                }

                val error = ErrorResponse(Resources.getSystem().getString(R.string.ERROR_SERVER)) as U
                failure(error)
                return
            } else if ( statusCode < 400 &&  call.body() != null) {

                val response = call.body() as T
                success(response)
            } else if (statusCode in 400..499 && call.errorBody() != null) {

                val errorResponse: U = gson.fromJson(
                    call.errorBody()!!.charStream(), U::class.java
                )
                failure(errorResponse)
            } else {

                val error = ErrorResponse(Resources.getSystem().getString(R.string.ERROR_SERVER)) as U
                failure(error)
                return
            }
        }
    }
}