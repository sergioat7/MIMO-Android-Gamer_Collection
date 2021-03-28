package es.upsa.mimo.gamercollection.fragments

import android.app.AlertDialog
import android.content.res.ColorStateList
import android.os.Bundle
import android.text.InputType
import android.view.*
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import es.upsa.mimo.gamercollection.R
import es.upsa.mimo.gamercollection.activities.LandingActivity
import es.upsa.mimo.gamercollection.extensions.setReadOnly
import es.upsa.mimo.gamercollection.fragments.base.BaseFragment
import es.upsa.mimo.gamercollection.utils.Constants
import es.upsa.mimo.gamercollection.viewmodelfactories.ProfileViewModelFactory
import es.upsa.mimo.gamercollection.viewmodels.ProfileViewModel
import kotlinx.android.synthetic.main.fragment_profile.*

class ProfileFragment : BaseFragment() {

    //MARK: - Private properties

    private lateinit var viewModel: ProfileViewModel

    // MARK: - Lifecycle methods

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_profile, container, false)
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

                showPopupConfirmationDialog(resources.getString(R.string.profile_delete_confirmation)) {
                    viewModel.deleteUser()
                }
                return true
            }
            R.id.action_logout -> {

                showPopupConfirmationDialog(resources.getString(R.string.profile_logout_confirmation)) {
                    viewModel.logout()
                }
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    //MARK: - Private methods

    private fun initializeUI() {

        val application = activity?.application
        viewModel = ViewModelProvider(
            this,
            ProfileViewModelFactory(application)
        ).get(ProfileViewModel::class.java)
        setupBindings()

        edit_text_user.setText(viewModel.userData.username)
        edit_text_user.setReadOnly(true, InputType.TYPE_NULL, 0)
        edit_text_password.setText(viewModel.userData.password)

        image_button_info.setOnClickListener {
            showPopupDialog(resources.getString(R.string.username_info))
        }

        image_button_password.setOnClickListener {
            Constants.showOrHidePassword(
                edit_text_password,
                image_button_password,
                Constants.isDarkMode(context)
            )
        }

        radio_button_en.isChecked = viewModel.language == Constants.ENGLISH_LANGUAGE_KEY
        radio_button_es.isChecked = viewModel.language == Constants.SPANISH_LANGUAGE_KEY

        spinner_sorting_keys.backgroundTintList =
            ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.colorPrimary))
        spinner_sorting_keys.adapter = Constants.getAdapter(
            requireContext(),
            resources.getStringArray(R.array.sorting_keys).toList(),
            true
        )
        val position =
            resources.getStringArray(R.array.sorting_keys_ids).indexOf(viewModel.sortingKey)
        spinner_sorting_keys.setSelection(position)

        switch_swipe_refresh.isChecked = viewModel.swipeRefresh

        val themeMode = getThemeMode()
        text_view_app_theme_value.text =
            resources.getStringArray(R.array.app_theme_values)[themeMode]
        text_view_app_theme_value.setOnClickListener {
            chooseThemeDialog()
        }

        button_save.setOnClickListener {

            val language =
                if (radio_button_en.isChecked) Constants.ENGLISH_LANGUAGE_KEY else Constants.SPANISH_LANGUAGE_KEY
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
    }

    private fun setupBindings() {

        viewModel.profileLoading.observe(viewLifecycleOwner, { isLoading ->

            if (isLoading) {
                showLoading()
            } else {
                hideLoading()
            }
        })

        viewModel.profileError.observe(viewLifecycleOwner, { error ->

            if (error == null) {

                launchActivity(LandingActivity::class.java)
                activity?.finish()
            } else {

                hideLoading()
                manageError(error)
            }
        })
    }

    private fun chooseThemeDialog() {

        val styles = resources.getStringArray(R.array.app_theme_values)
        val themeMode = getThemeMode()

        AlertDialog.Builder(context)
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

    private fun getThemeMode(): Int {

        return when (AppCompatDelegate.getDefaultNightMode()) {
            AppCompatDelegate.MODE_NIGHT_NO -> 1
            AppCompatDelegate.MODE_NIGHT_YES -> 2
            else -> 0
        }
    }
}
