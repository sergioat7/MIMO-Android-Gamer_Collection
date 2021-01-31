package es.upsa.mimo.gamercollection.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import es.upsa.mimo.gamercollection.R
import es.upsa.mimo.gamercollection.activities.MainActivity
import es.upsa.mimo.gamercollection.extensions.afterTextChanged
import es.upsa.mimo.gamercollection.extensions.clearErrors
import es.upsa.mimo.gamercollection.extensions.onFocusChange
import es.upsa.mimo.gamercollection.fragments.base.BaseFragment
import es.upsa.mimo.gamercollection.viewmodelfactories.RegisterViewModelFactory
import es.upsa.mimo.gamercollection.viewmodels.RegisterViewModel
import kotlinx.android.synthetic.main.fragment_register.*

class RegisterFragment : BaseFragment() {

    //MARK: - Private properties

    private lateinit var viewModel: RegisterViewModel

    // MARK: - Lifecycle methods

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

    //MARK: - Private methods

    private fun initializeUI() {

        val application = activity?.application
        viewModel = ViewModelProvider(this, RegisterViewModelFactory(application)).get(RegisterViewModel::class.java)
        setupBindings()

        edit_text_user.afterTextChanged {
            registerDataChanged()
        }
        edit_text_password.onFocusChange {
            registerDataChanged()
        }

        edit_text_password.afterTextChanged {
            registerDataChanged()
        }
        edit_text_password.onFocusChange {
            registerDataChanged()
        }

        edit_text_repeatPassword.afterTextChanged {
            registerDataChanged()
        }
        edit_text_repeatPassword.onFocusChange {
            registerDataChanged()
        }

        register_button.setOnClickListener {register()}
    }

    private fun setupBindings() {

        viewModel.registerFormState.observe(viewLifecycleOwner, {

            val registerState = it ?: return@observe

            edit_text_user.clearErrors()
            edit_text_password.clearErrors()
            edit_text_repeatPassword.clearErrors()

            register_button.isEnabled = registerState.isDataValid

            if (registerState.usernameError != null) {
                edit_text_user.error = getString(registerState.usernameError)
            }
            if (registerState.passwordError != null) {

                edit_text_password.error = getString(registerState.passwordError)
                edit_text_repeatPassword.error = getString(registerState.passwordError)
            }
        })

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

    private fun registerDataChanged() {

        viewModel.registerDataChanged(
            edit_text_user.text.toString(),
            edit_text_password.text.toString(),
            edit_text_repeatPassword.text.toString()
        )
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
