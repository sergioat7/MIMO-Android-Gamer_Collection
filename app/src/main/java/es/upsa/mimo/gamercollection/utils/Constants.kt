package es.upsa.mimo.gamercollection.utils

object Preferences {
    const val PREFERENCES_NAME = "preferences"
    const val ENCRYPTED_PREFERENCES_NAME = "encryptedPreferences"
    const val USER_DATA_PREFERENCES_NAME = "userData"
    const val AUTH_DATA_PREFERENCES_NAME = "authData"
    const val LANGUAGE_PREFERENCES_NAME = "language"
    const val ENGLISH_LANGUAGE_KEY = "en"
    const val SPANISH_LANGUAGE_KEY = "es"
    const val SORT_PARAM_PREFERENCES_NAME = "sortParam"
    const val DEFAULT_SORT_PARAM = "name"
    const val SWIPE_REFRESH_PREFERENCES_NAME = "swipeRefreshEnabled"
    const val GAME_NOTIFICATION_PREFERENCES_NAME = "gameNotificationLaunched_"
    const val THEME_MODE_PREFERENCES_NAME = "themeMode"
    const val SORT_ORDER_PREFERENCE_NAME = "sortOrder"
    const val NEW_CHANGES_POPUP_PREFERENCES_NAME = "newChanges"
}

object Constants {
    const val LOADING_DIALOG = "loadingDialog"
    const val DATE_FORMAT = "yyyy-MM-dd"
    const val EMPTY_VALUE = ""
    const val NO_VALUE = "-"

    fun isUserNameValid(username: String): Boolean {
        return username.isNotBlank()
    }

    fun isPasswordValid(password: String): Boolean {
        return password.length > 5
    }
}

object State {
    const val PENDING_STATE = "PENDING"
    const val IN_PROGRESS_STATE = "IN_PROGRESS"
    const val FINISHED_STATE = "FINISHED"
}

object Notifications {
    const val CHANNEL_ID = "GAMER_COLLECTION_NOTIFICATIONS_CHANNEL_ID"
    const val CHANNEL_GROUP = "GAMER_COLLECTION_NOTIFICATIONS_CHANNEL_GROUP"
}

enum class ScrollPosition {
    TOP, MIDDLE, END
}

enum class StatusBarStyle {
    PRIMARY,
    SECONDARY
}

enum class CustomInputType {
    TEXT,
    MULTI_LINE_TEXT,
    NUMBER,
    PASSWORD,
    URL,
    DATE,
    NONE
}

enum class CustomDropdownType {
    FORMAT,
    GENRE,
    PEGI,
    PLATFORM,
    STATE,
    SORT_PARAM,
    SORT_ORDER,
    APP_THEME
}