package es.upsa.mimo.gamercollection.utils

class Constants {
    companion object {

        // MARK: - SharedPref constants

        const val preferencesName = "preferences"
        const val userDataPrefName = "userData"
        const val authDataPrefName = "authData"

        // MARK: - Retrofit constants

        const val baseEndpoint = "https://videogames-collection-services.herokuapp.com/"
        const val acceptLanguageHeader = "Accept-Language"
        const val authorizationHeader = "Authorization"

        // MARK: - State constants

        const val pending = "PENDING"
        const val inProgress = "IN_PROGRESS"
        const val finished = "FINISHED"
    }
}