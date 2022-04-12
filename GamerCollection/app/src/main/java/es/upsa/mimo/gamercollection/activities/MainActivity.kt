package es.upsa.mimo.gamercollection.activities

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LiveData
import androidx.navigation.NavController
import es.upsa.mimo.gamercollection.R
import es.upsa.mimo.gamercollection.base.BaseActivity
import es.upsa.mimo.gamercollection.databinding.ActivityMainBinding
import es.upsa.mimo.gamercollection.extensions.setupWithNavController

class MainActivity : BaseActivity() {

    //region Private properties
    private lateinit var binding: ActivityMainBinding
    private var currentNavController: LiveData<NavController>? = null
    //endregion

    //region Lifecycle methods
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        setContentView(binding.root)

        if (savedInstanceState == null) {
            setupBottomNavigationBar()
        } // Else, need to wait for onRestoreInstanceState
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        // Now that BottomNavigationBar has restored its instance state
        // and its selectedItemId, we can proceed with setting up the
        // BottomNavigationBar with Navigation
        setupBottomNavigationBar()
    }

    override fun onSupportNavigateUp(): Boolean {
        return currentNavController?.value?.navigateUp() ?: false
    }
    //endregion

    //region Private methods
    /**
     * Called on first creation and when restoring state.
     */
    private fun setupBottomNavigationBar() {
        val bottomNavigationView = binding.navView
        val navGraphIds = listOf(
            R.navigation.nav_graph_games,
            R.navigation.nav_graph_search,
            R.navigation.nav_graph_sagas,
            R.navigation.nav_graph_settings
        )

        // Setup the bottom navigation view with a list of navigation graphs
        val controller = bottomNavigationView.setupWithNavController(
            navGraphIds = navGraphIds,
            fragmentManager = supportFragmentManager,
            containerId = R.id.nav_host_fragment,
            intent = intent
        )
        currentNavController = controller
    }
    //endregion
}
