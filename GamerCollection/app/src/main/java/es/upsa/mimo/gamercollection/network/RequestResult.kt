package es.upsa.mimo.gamercollection.network

import es.upsa.mimo.gamercollection.models.responses.ErrorResponse

sealed class RequestResult<out T> {
    object Success : RequestResult<Nothing>()
    data class JsonSuccess<out T>(val body: T?) : RequestResult<T>()
    data class Failure(val error: ErrorResponse) : RequestResult<Nothing>()
}