package es.upsa.mimo.gamercollection.presentation

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import com.google.android.material.snackbar.Snackbar
import com.google.android.play.core.install.model.InstallStatus
import dagger.hilt.android.AndroidEntryPoint
import es.upsa.mimo.gamercollection.R
import es.upsa.mimo.gamercollection.databinding.ActivityMainBinding
import es.upsa.mimo.gamercollection.extensions.setupWithNavController
import es.upsa.mimo.gamercollection.presentation.base.BaseActivity
import es.upsa.mimo.gamercollection.utils.InAppUpdateService
import javax.inject.Inject
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : BaseActivity() {

    //region Public properties
    @Inject
    lateinit var inAppUpdateService: InAppUpdateService
    //endregion

    //region Private properties
    private lateinit var binding: ActivityMainBinding
    private var currentNavController: LiveData<NavController>? = null
    //endregion

    //region Lifecycle methods
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        setContentView(binding.root)

        if (savedInstanceState == null) {
            setupBottomNavigationBar()
        } // Else, need to wait for onRestoreInstanceState

        lifecycleScope.launch {
            inAppUpdateService.installStatus.collect {
                if (it == InstallStatus.DOWNLOADED) {
                    inAppUpdateService.onResume()
                } else if (it == InstallStatus.DOWNLOADED + InstallStatus.INSTALLED) {
                    flexibleUpdateDownloadCompleted()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()

        inAppUpdateService.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()

        inAppUpdateService.onDestroy()
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        // Now that BottomNavigationBar has restored its instance state
        // and its selectedItemId, we can proceed with setting up the
        // BottomNavigationBar with Navigation
        setupBottomNavigationBar()
    }

    override fun onSupportNavigateUp(): Boolean = currentNavController?.value?.navigateUp() ?: false
    //endregion

    //region Private methods
    private fun setupBottomNavigationBar() {
        val bottomNavigationView = binding.navView
        val navGraphIds = listOf(
            R.navigation.nav_graph_games,
            R.navigation.nav_graph_search,
            R.navigation.nav_graph_sagas,
            R.navigation.nav_graph_settings,
        )

        // Setup the bottom navigation view with a list of navigation graphs
        val controller = bottomNavigationView.setupWithNavController(
            navGraphIds = navGraphIds,
            fragmentManager = supportFragmentManager,
            containerId = R.id.nav_host_fragment,
            intent = intent,
        )
        currentNavController = controller
    }

    private fun flexibleUpdateDownloadCompleted() {
        Snackbar
            .make(
                findViewById(android.R.id.content),
                getString(R.string.message_app_update_downloaded),
                Snackbar.LENGTH_INDEFINITE,
            ).apply {
                setAction(
                    getString(R.string.restart),
                ) { inAppUpdateService.completeUpdate() }
                setBackgroundTint(getColor(R.color.colorPrimary))
                setActionTextColor(getColor(R.color.colorSecondary))
                show()
            }
    }
    //endregion
}
