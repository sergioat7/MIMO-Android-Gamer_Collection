package es.upsa.mimo.gamercollection.utils

import com.google.android.gms.maps.model.LatLng
import es.upsa.mimo.gamercollection.data.remote.model.FormatResponse
import es.upsa.mimo.gamercollection.data.remote.model.GenreResponse
import es.upsa.mimo.gamercollection.data.remote.model.PlatformResponse
import es.upsa.mimo.gamercollection.data.remote.model.StateResponse

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
    const val SYNC_DIALOG = "syncDialog"
    const val POINT_UP = 0f
    const val POINT_DOWN = -180f
    const val DATABASE_NAME = "GamerCollection"
    const val DATE_FORMAT = "yyyy-MM-dd"
    const val EMPTY_VALUE = ""
    const val NO_VALUE = "-"
    const val NEXT_VALUE_SEPARATOR = ", "
    val DEFAULT_LOCATION = LatLng(40.4169019, -3.7056721)
    const val GOOGLE_USER_TEST = "googleTest"
    const val GOOGLE_PASSWORD_TEST = "d9MqzK3k1&07"

    var FORMATS = listOf(
        FormatResponse("DIGITAL", "Digital"),
        FormatResponse("PHYSICAL", "Physical"),
        FormatResponse("OTHER", "Other")
    )
    var GENRES = listOf(
        GenreResponse("ACTION", "Action"),
        GenreResponse("ACTION_ADVENTURE", "Action-Adventure"),
        GenreResponse("ADVENTURE", "Adventure"),
        GenreResponse("ARCADE", "Arcade"),
        GenreResponse("FIGHTING", "Fighting"),
        GenreResponse("HACK_AND_SLASH", "Hack and Slash"),
        GenreResponse("OTHER", "Other"),
        GenreResponse("PLATFORMS", "Platforms"),
        GenreResponse("PUZZLE", "Puzzle"),
        GenreResponse("RACING", "Racing"),
        GenreResponse("ROL", "Rol"),
        GenreResponse("SHOOTER", "Shooter"),
        GenreResponse("SIMULATOR", "Simulator"),
        GenreResponse("SOCIAL", "Social"),
        GenreResponse("SPORTS", "Sports"),
        GenreResponse("STRATEGY", "Strategy"),
        GenreResponse("SURVIVIAL_HORROR", "Survival Horror")
    )
    var PLATFORMS = listOf(
        PlatformResponse("PSONE", "PlayStation"),
        PlatformResponse("PS2", "PlayStation 2"),
        PlatformResponse("PS3", "PlayStation 3"),
        PlatformResponse("PS4", "PlayStation 4"),
        PlatformResponse("PS5", "PlayStation 5"),
        PlatformResponse("PSP", "PlayStation Portable"),
        PlatformResponse("PSVITA", "PlayStation Vita"),
        PlatformResponse("XBOX", "Xbox"),
        PlatformResponse("XBOX_360", "Xbox 360"),
        PlatformResponse("XBOX_ONE", "Xbox One"),
        PlatformResponse("XBOX_SERIES_X", "Xbox Series X"),
        PlatformResponse("PC", "PC"),
        PlatformResponse("SEGA", "Sega"),
        PlatformResponse("NES", "NES"),
        PlatformResponse("SUPER_NINTENDO", "Super Nintendo"),
        PlatformResponse("NINTENDO_64", "Nintendo 64"),
        PlatformResponse("GAME_CUBE", "Game Cube"),
        PlatformResponse("GAME_BOY", "Game Boy"),
        PlatformResponse("GBC", "Game Boy Color"),
        PlatformResponse("GBA", "Game Boy Advance"),
        PlatformResponse("GBA_SP", "Game Boy Advance SP"),
        PlatformResponse("NINTENDO_DS", "Nintendo DS"),
        PlatformResponse("NINTENDO_3DS", "Nintendo 3DS"),
        PlatformResponse("WII", "Wii"),
        PlatformResponse("WII_U", "Wii U"),
        PlatformResponse("NINTENDO_SWITCH", "Nintendo Switch"),
        PlatformResponse("OTHER", "Other")
    )
    var STATES = listOf(
        StateResponse("PENDING", "Pending"),
        StateResponse("IN_PROGRESS", "In Progress"),
        StateResponse("FINISHED", "Finished")
    )

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