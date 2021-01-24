package es.upsa.mimo.gamercollection.fragments

import android.content.res.ColorStateList
import android.os.Bundle
import android.text.InputType
import android.view.*
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

        when(item.itemId) {
            R.id.action_synchronize -> {

                openSyncPopup()
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
        viewModel = ViewModelProvider(this, ProfileViewModelFactory(application)).get(ProfileViewModel::class.java)
        setupBindings()

        edit_text_user.setText(viewModel.userData.username)
        edit_text_user.setReadOnly(true, InputType.TYPE_NULL, 0)
        edit_text_password.setText(viewModel.userData.password)

        radio_button_en.isChecked = viewModel.language == "en"
        radio_button_es.isChecked = viewModel.language == "es"

        var position = 0
        viewModel.sortingKey.let { sortingKey ->
            position = resources.getStringArray(R.array.sorting_keys_ids).indexOf(sortingKey)
        }
        spinner_sorting_keys.setSelection(position)
        spinner_sorting_keys.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.color2))
        spinner_sorting_keys.adapter = Constants.getAdapter(
            requireContext(),
            resources.getStringArray(R.array.sorting_keys).toList(),
            true
        )

        switch_swipe_refresh.isChecked = viewModel.swipeRefresh

        button_change_password.setOnClickListener {

            val language = if(radio_button_en.isChecked) "en" else "es"
            val sortingKey = resources.getStringArray(R.array.sorting_keys_ids)[spinner_sorting_keys.selectedItemPosition]
            viewModel.save(
                edit_text_password.text.toString(),
                language,
                sortingKey,
                switch_swipe_refresh.isChecked
            )
        }
        button_delete_user.setOnClickListener {

            showPopupConfirmationDialog(resources.getString(R.string.profile_delete_confirmation)) {
                viewModel.deleteUser()
            }
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
}
