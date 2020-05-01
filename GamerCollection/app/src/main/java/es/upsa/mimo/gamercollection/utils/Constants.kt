package es.upsa.mimo.gamercollection.utils

import android.os.Build
import android.os.LocaleList
import java.util.*

class Constants {
    companion object {

        const val preferencesName = "preferences"
        const val newInstallationPrefName = "newInstallation"
        const val userDataPrefName = "userData"
        const val authDataPrefName = "authData"
        const val baseEndpoint = "https://videogames-collection-services.herokuapp.com/"
        const val acceptLanguageHeader = "Accept-Language"

        fun getLanguage(): String {
            return Locale.getDefault().language
        }
    }
}