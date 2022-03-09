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
import es.upsa.mimo.gamercollection.databinding.FragmentProfileBinding
import es.upsa.mimo.gamercollection.extensions.setReadOnly
import es.upsa.mimo.gamercollection.utils.Constants
import es.upsa.mimo.gamercollection.utils.Preferences
import es.upsa.mimo.gamercollection.utils.StatusBarStyle
import es.upsa.mimo.gamercollection.viewmodelfactories.ProfileViewModelFactory
import es.upsa.mimo.gamercollection.viewmodels.ProfileViewModel
import kotlinx.android.synthetic.main.fragment_profile.*

class ProfileFragment : BindingFragment<FragmentProfileBinding>() {

    //region Protected properties
    override val statusBarStyle = StatusBarStyle.SECONDARY
    //endregion

    //region Private properties
    private lateinit var viewModel: ProfileViewModel
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
        inflater.inflate(R.menu.profile_toolbar_menu, menu)
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

                text_view_app_theme_value.text = when (value) {
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
            if (radio_button_en.isChecked) Preferences.ENGLISH_LANGUAGE_KEY
            else Preferences.SPANISH_LANGUAGE_KEY
        val sortingKey =
            resources.getStringArray(R.array.sorting_keys_ids)[spinner_sorting_keys.selectedItemPosition]
        val themeMode =
            resources.getStringArray(R.array.app_theme_values)
                .indexOf(text_view_app_theme_value.text.toString())
        viewModel.save(
            edit_text_password.text.toString(),
            language,
            sortingKey,
            switch_swipe_refresh.isChecked,
            themeMode
        )
    }
    //endregion

    //region Private methods
    private fun initializeUI() {

        val application = activity?.application
        viewModel = ViewModelProvider(
            this,
            ProfileViewModelFactory(application)
        )[ProfileViewModel::class.java]
        setupBindings()

        with(binding) {

            editTextUser.setReadOnly(true, InputType.TYPE_NULL, 0)

            imageButtonPassword.setOnClickListener {
                Constants.showOrHidePassword(
                    edit_text_password,
                    image_button_password
                )
            }

            radioButtonEn.isChecked =
                this@ProfileFragment.viewModel.language == Preferences.ENGLISH_LANGUAGE_KEY
            radioButtonEs.isChecked =
                this@ProfileFragment.viewModel.language == Preferences.SPANISH_LANGUAGE_KEY

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
                    resources.getStringArray(R.array.sorting_keys).toList(),
                    true
                )
                setSelection(
                    resources.getStringArray(R.array.sorting_keys_ids)
                        .indexOf(this@ProfileFragment.viewModel.sortParam)
                )
            }

            textViewAppThemeValue.text =
                resources.getStringArray(R.array.app_theme_values)[getThemeMode()]

            fragment = this@ProfileFragment
            viewModel = this@ProfileFragment.viewModel
            lifecycleOwner = this@ProfileFragment
        }
    }

    private fun setupBindings() {

        viewModel.profileLoading.observe(viewLifecycleOwner) { isLoading ->

            if (isLoading) {
                showLoading()
            } else {
                hideLoading()
            }
        }

        viewModel.profileError.observe(viewLifecycleOwner) { error ->

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
