package es.upsa.mimo.gamercollection.utils

import com.google.android.gms.maps.model.LatLng
import es.upsa.mimo.gamercollection.models.FormatModel
import es.upsa.mimo.gamercollection.models.GenreModel
import es.upsa.mimo.gamercollection.models.PlatformModel
import es.upsa.mimo.gamercollection.models.StateModel

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
    const val VERSION_PREFERENCES_NAME = "version"
    const val THEME_MODE_PREFERENCES_NAME = "themeMode"
    const val SORT_ORDER_PREFERENCE_NAME = "sortOrder"
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

    var FORMATS = listOf(
        FormatModel("DIGITAL", "Digital"),
        FormatModel("PHYSICAL", "Physical"),
        FormatModel("OTHER", "Other")
    )
    var GENRES = listOf(
        GenreModel("ACTION", "Action"),
        GenreModel("ACTION_ADVENTURE", "Action-Adventure"),
        GenreModel("ADVENTURE", "Adventure"),
        GenreModel("ARCADE", "Arcade"),
        GenreModel("FIGHTING", "Fighting"),
        GenreModel("HACK_AND_SLASH", "Hack and Slash"),
        GenreModel("OTHER", "Other"),
        GenreModel("PLATFORMS", "Platforms"),
        GenreModel("PUZZLE", "Puzzle"),
        GenreModel("RACING", "Racing"),
        GenreModel("ROL", "Rol"),
        GenreModel("SHOOTER", "Shooter"),
        GenreModel("SIMULATOR", "Simulator"),
        GenreModel("SOCIAL", "Social"),
        GenreModel("SPORTS", "Sports"),
        GenreModel("STRATEGY", "Strategy"),
        GenreModel("SURVIVIAL_HORROR", "Survival Horror")
    )
    var PLATFORMS = listOf(
        PlatformModel("PSONE", "PlayStation"),
        PlatformModel("PS2", "PlayStation 2"),
        PlatformModel("PS3", "PlayStation 3"),
        PlatformModel("PS4", "PlayStation 4"),
        PlatformModel("PS5", "PlayStation 5"),
        PlatformModel("PSP", "PlayStation Portable"),
        PlatformModel("PSVITA", "PlayStation Vita"),
        PlatformModel("XBOX", "Xbox"),
        PlatformModel("XBOX_360", "Xbox 360"),
        PlatformModel("XBOX_ONE", "Xbox One"),
        PlatformModel("XBOX_SERIES_X", "Xbox Series X"),
        PlatformModel("PC", "PC"),
        PlatformModel("SEGA", "Sega"),
        PlatformModel("NES", "NES"),
        PlatformModel("SUPER_NINTENDO", "Super Nintendo"),
        PlatformModel("NINTENDO_64", "Nintendo 64"),
        PlatformModel("GAME_CUBE", "Game Cube"),
        PlatformModel("GAME_BOY", "Game Boy"),
        PlatformModel("GBC", "Game Boy Color"),
        PlatformModel("GBA", "Game Boy Advance"),
        PlatformModel("GBA_SP", "Game Boy Advance SP"),
        PlatformModel("NINTENDO_DS", "Nintendo DS"),
        PlatformModel("NINTENDO_3DS", "Nintendo 3DS"),
        PlatformModel("WII", "Wii"),
        PlatformModel("WII_U", "Wii U"),
        PlatformModel("NINTENDO_SWITCH", "Nintendo Switch"),
        PlatformModel("OTHER", "Other")
    )
    var STATES = listOf(
        StateModel("PENDING", "Pending"),
        StateModel("IN_PROGRESS", "In Progress"),
        StateModel("FINISHED", "Finished")
    )

    fun isUserNameValid(username: String): Boolean {
        return username.isNotBlank()
    }

    fun isPasswordValid(password: String): Boolean {
        return password.length > 3
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