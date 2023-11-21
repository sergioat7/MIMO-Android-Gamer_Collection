package es.upsa.mimo.gamercollection.ui.settings

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import es.upsa.mimo.gamercollection.R
import es.upsa.mimo.gamercollection.ui.landing.LandingActivity
import es.upsa.mimo.gamercollection.ui.base.BindingFragment
import es.upsa.mimo.gamercollection.databinding.FragmentSettingsBinding
import es.upsa.mimo.gamercollection.extensions.doAfterTextChanged
import es.upsa.mimo.gamercollection.extensions.getPosition
import es.upsa.mimo.gamercollection.extensions.getValue
import es.upsa.mimo.gamercollection.extensions.setEndIconOnClickListener
import es.upsa.mimo.gamercollection.extensions.setError
import es.upsa.mimo.gamercollection.extensions.setValue
import es.upsa.mimo.gamercollection.utils.CustomDropdownType
import es.upsa.mimo.gamercollection.utils.Preferences
import es.upsa.mimo.gamercollection.utils.StatusBarStyle
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

@AndroidEntryPoint
class SettingsFragment : BindingFragment<FragmentSettingsBinding>() {

    //region Protected properties
    override val statusBarStyle = StatusBarStyle.SECONDARY
    override val hasOptionsMenu = true
    //endregion

    //region Private properties
    private val viewModel: SettingsViewModel by viewModels()
    private lateinit var openFileLauncher: ActivityResultLauncher<Intent>
    private lateinit var newFileLauncher: ActivityResultLauncher<Intent>
    //endregion

    //region Lifecycle methods
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        toolbar = binding.toolbar
        initializeUi()
    }

    @Deprecated("Deprecated in Java")
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.clear()
        inflater.inflate(R.menu.settings_toolbar_menu, menu)
    }

    @Deprecated("Deprecated in Java")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.action_import -> {
                showPopupConfirmationDialog(
                    resources.getString(R.string.import_confirmation),
                    acceptHandler = {

                        val intent = Intent(Intent.ACTION_GET_CONTENT)
                        intent.type = "*/*"
                        openFileLauncher.launch(intent)
                    })
                return true
            }

            R.id.action_export -> {
                showPopupConfirmationDialog(
                    resources.getString(R.string.export_confirmation),
                    acceptHandler = {

                        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
                            addCategory(Intent.CATEGORY_OPENABLE)
                            type = "text/txt"
                            putExtra(Intent.EXTRA_TITLE, "gamercollection_database_backup.txt")
                        }
                        newFileLauncher.launch(intent)
                    })
                return true
            }

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
            this.textInputLayoutPassword.textInputEditText.clearFocus()
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

        openFileLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    result.data?.data?.let { uri ->

                        try {
                            val inputStream = context?.contentResolver?.openInputStream(uri)
                            val reader = BufferedReader(InputStreamReader(inputStream))
                            val jsonData = reader.readLine()
                            inputStream?.close()
                            viewModel.importData(jsonData)
                            val message = resources.getString(R.string.data_imported)
                            showPopupDialog(message)
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }
            }
        newFileLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    result.data?.data?.let { uri ->

                        val data = viewModel.getDataToExport()
                        context?.contentResolver?.openOutputStream(uri)
                            ?.use { outputStream ->
                                outputStream.write(data.toByteArray())
                                outputStream.close()
                            }
                        val message = resources.getString(R.string.file_created)
                        showPopupDialog(message)
                    }
                }
            }

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
