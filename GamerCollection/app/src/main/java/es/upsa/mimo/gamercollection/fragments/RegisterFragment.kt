package es.upsa.mimo.gamercollection.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import es.upsa.mimo.gamercollection.R
import es.upsa.mimo.gamercollection.activities.MainActivity
import es.upsa.mimo.gamercollection.fragments.base.BaseFragment
import es.upsa.mimo.gamercollection.injection.GamerCollectionApplication
import es.upsa.mimo.gamercollection.models.AuthData
import es.upsa.mimo.gamercollection.models.UserData
import es.upsa.mimo.gamercollection.network.apiClient.*
import es.upsa.mimo.gamercollection.repositories.FormatRepository
import es.upsa.mimo.gamercollection.repositories.GenreRepository
import es.upsa.mimo.gamercollection.repositories.PlatformRepository
import es.upsa.mimo.gamercollection.repositories.StateRepository
import es.upsa.mimo.gamercollection.utils.SharedPreferencesHandler
import es.upsa.mimo.gamercollection.viewmodelfactories.LoginViewModelFactory
import es.upsa.mimo.gamercollection.viewmodelfactories.RegisterViewModelFactory
import es.upsa.mimo.gamercollection.viewmodels.LoginViewModel
import es.upsa.mimo.gamercollection.viewmodels.RegisterViewModel
import kotlinx.android.synthetic.main.fragment_register.*
import javax.inject.Inject

class RegisterFragment : BaseFragment() {

    private lateinit var viewModel: RegisterViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_register, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeUI()
    }

    //MARK: - Private functions

    private fun initializeUI() {

        val application = activity?.application
        viewModel = ViewModelProvider(this, RegisterViewModelFactory(application)).get(RegisterViewModel::class.java)
        setupBindings()

        register_button.setOnClickListener {register()}
    }

    private fun setupBindings() {

        viewModel.registerLoading.observe(viewLifecycleOwner, { isLoading ->

            if (isLoading) {
                showLoading()
            } else {
                hideLoading()
            }
        })

        viewModel.registerError.observe(viewLifecycleOwner, { error ->

            if (error == null) {
                launchActivity(MainActivity::class.java)
            } else {

                hideLoading()
                manageError(error)
            }
        })
    }

    private fun register() {

        val username = edit_text_user.text.toString()
        val password = edit_text_password.text.toString()
        val repeatPassword = edit_text_repeatPassword.text.toString()

        if (username.isEmpty() || password.isEmpty() || repeatPassword.isEmpty()) {
            showPopupDialog(resources.getString(R.string.error_registration_empty_data))
            return
        }

        if (password != repeatPassword) {
            showPopupDialog(resources.getString(R.string.error_registration_different_passwords))
            return
        }

        viewModel.register(username, password)
    }
}
