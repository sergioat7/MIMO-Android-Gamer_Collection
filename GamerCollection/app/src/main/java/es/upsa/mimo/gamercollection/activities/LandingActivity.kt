package es.upsa.mimo.gamercollection.activities

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import com.google.gson.Gson
import es.upsa.mimo.gamercollection.R
import es.upsa.mimo.gamercollection.base.BaseActivity
import es.upsa.mimo.gamercollection.models.FormatModel
import es.upsa.mimo.gamercollection.models.GenreModel
import es.upsa.mimo.gamercollection.models.PlatformModel
import es.upsa.mimo.gamercollection.models.StateModel
import es.upsa.mimo.gamercollection.utils.Constants
import es.upsa.mimo.gamercollection.utils.Notifications
import es.upsa.mimo.gamercollection.viewmodels.LandingViewModel
import org.json.JSONObject
import java.util.*

class LandingActivity : BaseActivity() {

    //region Private properties
     private val viewModel: LandingViewModel by viewModels()
    //endregion

    //region Lifecycle methods
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initializeUI()
    }
    //endregion

    //region Private methods
    private fun initializeUI() {

        setupBindings()

        configLanguage()
        fetchRemoteConfigValues()
        createNotificationChannel()
        viewModel.checkTheme()

        if (!viewModel.newChangesPopupShown) {
            showPopupActionDialog(getString(R.string.new_version_changes), acceptHandler = {
                viewModel.checkIsLoggedIn()
            })
        } else {
            viewModel.checkIsLoggedIn()
        }
    }

    private fun setupBindings() {

        viewModel.landingClassToStart.observe(this) { cls: Class<*>? ->

            val intent = Intent(this, cls)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }

    private fun configLanguage() {

        val conf = resources.configuration
        conf.setLocale(Locale(viewModel.language))
        resources.updateConfiguration(conf, resources.displayMetrics)
    }

    private fun createNotificationChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_name)
            val description = getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(Notifications.CHANNEL_ID, name, importance)
            channel.description = description
            val notificationManager = getSystemService(
                NotificationManager::class.java
            )
            notificationManager?.createNotificationChannel(channel)
        }
    }

    private fun fetchRemoteConfigValues() {

        val remoteConfig = Firebase.remoteConfig.apply {
            setConfigSettingsAsync(
                remoteConfigSettings {
                    minimumFetchIntervalInSeconds = 3600
                }
            )
        }

        setupFormats(remoteConfig.getString("formats"))
        setupGenres(remoteConfig.getString("genres"))
        setupPlatforms(remoteConfig.getString("platforms"))
        setupStates(remoteConfig.getString("states"))

        remoteConfig.fetchAndActivate().addOnCompleteListener(this) {

            setupFormats(remoteConfig.getString("formats"))
            setupGenres(remoteConfig.getString("genres"))
            setupPlatforms(remoteConfig.getString("platforms"))
            setupStates(remoteConfig.getString("states"))
        }
    }

    private fun setupFormats(formatsString: String) {

        if (formatsString.isNotEmpty()) {
            var formats = listOf<FormatModel>()
            try {
                val languagedFormats =
                    JSONObject(formatsString).get(viewModel.language).toString()
                formats =
                    Gson().fromJson(languagedFormats, Array<FormatModel>::class.java).asList()
            } catch (e: Exception) {
                Log.e("LandingActivity", e.message ?: "")
            }

            Constants.FORMATS = formats
        }
    }

    private fun setupGenres(genresString: String) {

        if (genresString.isNotEmpty()) {
            var genres = listOf<GenreModel>()
            try {
                val languagedGenres =
                    JSONObject(genresString).get(viewModel.language).toString()
                genres =
                    Gson().fromJson(languagedGenres, Array<GenreModel>::class.java).asList()
            } catch (e: Exception) {
                Log.e("LandingActivity", e.message ?: "")
            }

            Constants.GENRES = genres
        }
    }

    private fun setupPlatforms(platformsString: String) {

        if (platformsString.isNotEmpty()) {
            var platforms = listOf<PlatformModel>()
            try {
                val languagedPlatforms =
                    JSONObject(platformsString).get(viewModel.language).toString()
                platforms =
                    Gson().fromJson(languagedPlatforms, Array<PlatformModel>::class.java)
                        .asList()
            } catch (e: Exception) {
                Log.e("LandingActivity", e.message ?: "")
            }

            Constants.PLATFORMS = platforms
        }
    }

    private fun setupStates(statesString: String) {

        if (statesString.isNotEmpty()) {
            var states = listOf<StateModel>()
            try {
                val languagedStates =
                    JSONObject(statesString).get(viewModel.language).toString()
                states =
                    Gson().fromJson(languagedStates, Array<StateModel>::class.java).asList()
            } catch (e: Exception) {
                Log.e("LandingActivity", e.message ?: "")
            }

            Constants.STATES = states
        }
    }
    //endregion
}