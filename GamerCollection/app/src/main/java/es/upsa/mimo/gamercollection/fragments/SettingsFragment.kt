package es.upsa.mimo.gamercollection.fragments

import android.content.res.ColorStateList
import android.os.Bundle
import android.text.InputType
import android.view.*
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import es.upsa.mimo.gamercollection.R
import es.upsa.mimo.gamercollection.activities.LandingActivity
import es.upsa.mimo.gamercollection.base.BindingFragment
import es.upsa.mimo.gamercollection.databinding.FragmentSettingsBinding
import es.upsa.mimo.gamercollection.extensions.setReadOnly
import es.upsa.mimo.gamercollection.utils.Constants
import es.upsa.mimo.gamercollection.utils.Preferences
import es.upsa.mimo.gamercollection.utils.StatusBarStyle
import es.upsa.mimo.gamercollection.viewmodelfactories.SettingsViewModelFactory
import es.upsa.mimo.gamercollection.viewmodels.SettingsViewModel

class SettingsFragment : BindingFragment<FragmentSettingsBinding>() {

    //region Protected properties
    override val statusBarStyle = StatusBarStyle.SECONDARY
    //endregion

    //region Private properties
    private lateinit var viewModel: SettingsViewModel
    //endregion

    //region Lifecycle methods
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeUI()
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
    //endregion

    //region Public methods
    fun showMessage(message: String) {
        showPopupDialog(message)
    }

    fun chooseThemeDialog() {

        val styles = resources.getStringArray(R.array.app_theme_values)
        val themeMode = getThemeMode()

        MaterialAlertDialogBuilder(requireContext())
            .setTitle(resources.getString(R.string.choose_a_theme))
            .setSingleChoiceItems(styles, themeMode) { dialog, value ->

                binding.textViewAppThemeValue.text = when (value) {
                    1 -> styles[1]
                    2 -> styles[2]
                    else -> styles[0]
                }
                dialog.dismiss()
            }
            .show()
    }

    fun save() {

        val language =
            if (binding.radioButtonEn.isChecked) Preferences.ENGLISH_LANGUAGE_KEY
            else Preferences.SPANISH_LANGUAGE_KEY
        val sortingKey =
            resources.getStringArray(R.array.sort_param_keys)[binding.spinnerSortingKeys.selectedItemPosition]
        val themeMode =
            resources.getStringArray(R.array.app_theme_values)
                .indexOf(binding.textViewAppThemeValue.text.toString())
        viewModel.save(
            binding.editTextPassword.text.toString(),
            language,
            sortingKey,
            binding.switchSwipeRefresh.isChecked,
            themeMode
        )
    }
    //endregion

    //region Private methods
    private fun initializeUI() {

        val application = activity?.application
        viewModel = ViewModelProvider(
            this,
            SettingsViewModelFactory(application)
        )[SettingsViewModel::class.java]
        setupBindings()

        with(binding) {

            editTextUser.setReadOnly(true, InputType.TYPE_NULL, 0)

            imageButtonPassword.setOnClickListener {
                Constants.showOrHidePassword(
                    binding.editTextPassword,
                    binding.imageButtonPassword
                )
            }

            radioButtonEn.isChecked =
                this@SettingsFragment.viewModel.language == Preferences.ENGLISH_LANGUAGE_KEY
            radioButtonEs.isChecked =
                this@SettingsFragment.viewModel.language == Preferences.SPANISH_LANGUAGE_KEY

            spinnerSortingKeys.apply {
                backgroundTintList =
                    ColorStateList.valueOf(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.colorPrimary
                        )
                    )
                adapter = Constants.getAdapter(
                    requireContext(),
                    resources.getStringArray(R.array.sort_param_values).toList(),
                    true
                )
                setSelection(
                    resources.getStringArray(R.array.sort_param_keys)
                        .indexOf(this@SettingsFragment.viewModel.sortParam)
                )
            }

            textViewAppThemeValue.text =
                resources.getStringArray(R.array.app_theme_values)[getThemeMode()]

            fragment = this@SettingsFragment
            viewModel = this@SettingsFragment.viewModel
            lifecycleOwner = this@SettingsFragment
        }
    }

    private fun setupBindings() {

        viewModel.settingsLoading.observe(viewLifecycleOwner) { isLoading ->

            if (isLoading) {
                showLoading()
            } else {
                hideLoading()
            }
        }

        viewModel.settingsError.observe(viewLifecycleOwner) { error ->

            if (error == null) {

                launchActivity(LandingActivity::class.java)
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
    //endregion
}
