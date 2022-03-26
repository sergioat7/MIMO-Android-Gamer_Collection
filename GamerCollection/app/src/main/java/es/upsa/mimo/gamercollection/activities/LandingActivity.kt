package es.upsa.mimo.gamercollection.activities

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import es.upsa.mimo.gamercollection.R
import es.upsa.mimo.gamercollection.base.BaseActivity
import es.upsa.mimo.gamercollection.utils.Notifications
import es.upsa.mimo.gamercollection.viewmodelfactories.LandingViewModelFactory
import es.upsa.mimo.gamercollection.viewmodels.LandingViewModel
import java.util.*

class LandingActivity : BaseActivity() {

    //region Private properties
    private lateinit var viewModel: LandingViewModel
    //endregion

    //region Lifecycle methods
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initializeUI()
    }
    //endregion

    //region Private methods
    private fun initializeUI() {

        viewModel = ViewModelProvider(
            this,
            LandingViewModelFactory(application)
        )[LandingViewModel::class.java]
        setupBindings()

        configLanguage()
        fetchRemoteConfigValues()
        createNotificationChannel()
        viewModel.checkVersion()
        viewModel.checkTheme()
    }

    private fun setupBindings() {

        viewModel.landingClassToStart.observe(this) { cls: Class<*>? ->
            val intent = Intent(this, cls)
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
            //TODO: save formats
        }
    }

    private fun setupGenres(genresString: String) {

        if (genresString.isNotEmpty()) {
            //TODO: save genres
        }
    }

    private fun setupPlatforms(platformsString: String) {

        if (platformsString.isNotEmpty()) {
            //TODO: save platforms
        }
    }

    private fun setupStates(statesString: String) {

        if (statesString.isNotEmpty()) {
            //TODO: save states
        }
    }
    //endregion
}