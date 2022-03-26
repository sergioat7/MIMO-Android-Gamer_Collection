package es.upsa.mimo.gamercollection.activities

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import es.upsa.mimo.gamercollection.R
import es.upsa.mimo.gamercollection.base.BaseActivity
import es.upsa.mimo.gamercollection.utils.Notifications
import es.upsa.mimo.gamercollection.viewmodelfactories.LandingViewModelFactory
import es.upsa.mimo.gamercollection.viewmodels.LandingViewModel
import java.util.*

class LandingActivity : BaseActivity() {

    //region Private properties
    private var viewModel: LandingViewModel? = null
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
        createNotificationChannel()
        viewModel!!.checkVersion()
        viewModel!!.checkTheme()
    }

    private fun setupBindings() {
        viewModel!!.landingClassToStart.observe(this) { cls: Class<*>? ->
            val intent = Intent(this, cls)
            startActivity(intent)
        }
    }

    private fun configLanguage() {
        val language = viewModel!!.language
        val conf = resources.configuration
        conf.setLocale(Locale(language))
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
    } //endregion
}