package es.upsa.mimo.gamercollection.presentation.landing

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.play.core.install.model.InstallStatus
import dagger.hilt.android.AndroidEntryPoint
import es.upsa.mimo.gamercollection.R
import es.upsa.mimo.gamercollection.presentation.base.BaseActivity
import es.upsa.mimo.gamercollection.utils.InAppUpdateService
import es.upsa.mimo.gamercollection.utils.Notifications
import java.util.*
import javax.inject.Inject
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LandingActivity : BaseActivity() {

    //region Public properties
    @Inject
    lateinit var inAppUpdateService: InAppUpdateService
    //endregion

    //region Private properties
    private val viewModel: LandingViewModel by viewModels()
    //endregion

    //region Lifecycle methods
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initializeUI()
    }

    override fun onDestroy() {
        super.onDestroy()

        inAppUpdateService.onDestroy()
    }
    //endregion

    //region Private methods
    private fun initializeUI() {
        setupBindings()

        configLanguage()
        viewModel.fetchRemoteConfigValues()
        createNotificationChannel()
        viewModel.checkTheme()

        inAppUpdateService.checkVersion()
        lifecycleScope.launch {
            inAppUpdateService.installStatus.collect {
                when (it) {
                    InstallStatus.DOWNLOADING,
                    InstallStatus.DOWNLOADED,
                    InstallStatus.INSTALLED,
                    InstallStatus.CANCELED,
                    -> {
                        if (it == InstallStatus.CANCELED &&
                            inAppUpdateService.isImmediateUpdate()
                        ) {
                            finish()
                        } else {
                            if (!viewModel.newChangesPopupShown) {
                                showPopupActionDialog(
                                    getString(
                                        R.string.new_version_changes,
                                    ),
                                    acceptHandler = {
                                        viewModel.checkIsLoggedIn()
                                    },
                                )
                            } else {
                                viewModel.checkIsLoggedIn()
                            }
                            inAppUpdateService.onDestroy()
                        }
                    }
                    InstallStatus.FAILED -> {
                        inAppUpdateService.checkVersion()
                    }
                    else -> {
                        Unit
                    }
                }
            }
        }
    }

    private fun setupBindings() {
        lifecycleScope.launch {
            viewModel.landingClassToStart.filterNotNull().collect { cls: Class<*> ->

                val intent = Intent(this@LandingActivity, cls)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
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
                NotificationManager::class.java,
            )
            notificationManager?.createNotificationChannel(channel)
        }
    }
    //endregion
}