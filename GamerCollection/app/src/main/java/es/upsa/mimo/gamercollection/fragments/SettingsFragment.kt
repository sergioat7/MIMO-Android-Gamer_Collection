package es.upsa.mimo.gamercollection.fragments

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModelProvider
import es.upsa.mimo.gamercollection.R
import es.upsa.mimo.gamercollection.activities.LandingActivity
import es.upsa.mimo.gamercollection.base.BindingFragment
import es.upsa.mimo.gamercollection.databinding.FragmentSettingsBinding
import es.upsa.mimo.gamercollection.extensions.*
import es.upsa.mimo.gamercollection.utils.CustomDropdownType
import es.upsa.mimo.gamercollection.utils.Preferences
import es.upsa.mimo.gamercollection.utils.StatusBarStyle
import es.upsa.mimo.gamercollection.viewmodelfactories.SettingsViewModelFactory
import es.upsa.mimo.gamercollection.viewmodels.SettingsViewModel

class SettingsFragment : BindingFragment<FragmentSettingsBinding>() {

    //region Protected properties
    override val statusBarStyle = StatusBarStyle.SECONDARY
    override val hasOptionsMenu = true
    //endregion

    //region Private properties
    private lateinit var viewModel: SettingsViewModel
    //endregion

    //region Lifecycle methods
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeUi()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.clear()
        inflater.inflate(R.menu.settings_toolbar_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.action_delete -> {

                showPopupConfirmationDialog(
                    resources.getString(R.string.profile_delete_confirmation),
                    {
                        viewModel.deleteUser()
                    })
                return true
            }
            R.id.action_logout -> {

                showPopupConfirmationDialog(
                    resources.getString(R.string.profile_logout_confirmation),
                    {
                        viewModel.logout()
                    })
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()

        binding.textInputLayoutPassword.doAfterTextChanged {
            viewModel.profileDataChanged(it.toString())
        }
        setupDropdowns()
    }
    //endregion

    //region Public methods
    fun save() {
        with(binding) {

            val language =
                if (radioButtonEn.isChecked) Preferences.ENGLISH_LANGUAGE_KEY
                else Preferences.SPANISH_LANGUAGE_KEY
            val sortParam =
                resources.getStringArray(R.array.sort_param_keys)[dropdownTextInputLayoutSortParams.getPosition()]
            val isSortAscending = dropdownTextInputLayoutSortOrders.getPosition() == 0
            val themeMode = dropdownTextInputLayoutAppTheme.getPosition()
            this@SettingsFragment.viewModel.save(
                textInputLayoutPassword.getValue(),
                language,
                sortParam,
                isSortAscending,
                binding.switchSwipeRefresh.isChecked,
                themeMode
            )
        }
    }
    //endregion

    //region Protected methods
    override fun initializeUi() {
        super.initializeUi()

        val application = activity?.application
        viewModel = ViewModelProvider(
            this,
            SettingsViewModelFactory(application)
        )[SettingsViewModel::class.java]
        setupBindings()

        binding.textInputLayoutUsername.setEndIconOnClickListener {
            showPopupDialog(resources.getString(R.string.username_info))
        }
        binding.fragment = this
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
    }
    //endregion

    //region Protected methods
    private fun setupBindings() {

        viewModel.settingsForm.observe(viewLifecycleOwner) {

            binding.textInputLayoutPassword.setError("")
            val passwordError = it ?: return@observe
            binding.textInputLayoutPassword.setError(getString(passwordError))
        }

        viewModel.settingsLoading.observe(viewLifecycleOwner) { isLoading ->

            if (isLoading) {
                showLoading()
            } else {
                hideLoading()
            }
        }

        viewModel.settingsError.observe(viewLifecycleOwner) { error ->

            if (error == null) {

                launchActivity(LandingActivity::class.java, true)
                activity?.finish()
            } else {

                hideLoading()
                manageError(error)
            }
        }
    }

    private fun getThemeMode(): Int {

        return when (AppCompatDelegate.getDefaultNightMode()) {
            AppCompatDelegate.MODE_NIGHT_NO -> 1
            AppCompatDelegate.MODE_NIGHT_YES -> 2
            else -> 0
        }
    }

    private fun setupDropdowns() {

        binding.dropdownTextInputLayoutSortParams.setValue(
            viewModel.sortParam,
            CustomDropdownType.SORT_PARAM
        )

        val sortOrderKeys = resources.getStringArray(R.array.sort_order_keys).toList()
        binding.dropdownTextInputLayoutSortOrders.setValue(
            if (viewModel.isSortOrderAscending) sortOrderKeys[0] else sortOrderKeys[1],
            CustomDropdownType.SORT_ORDER
        )

        val appThemes = resources.getStringArray(R.array.app_theme_values).toList()
        binding.dropdownTextInputLayoutAppTheme.setValue(
            appThemes[getThemeMode()],
            CustomDropdownType.APP_THEME
        )
    }
    //endregion
}
