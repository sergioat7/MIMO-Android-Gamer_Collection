package es.upsa.mimo.gamercollection.fragments

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import es.upsa.mimo.gamercollection.R
import es.upsa.mimo.gamercollection.activities.MainActivity
import es.upsa.mimo.gamercollection.base.BindingFragment
import es.upsa.mimo.gamercollection.databinding.FragmentRegisterBinding
import es.upsa.mimo.gamercollection.extensions.afterTextChanged
import es.upsa.mimo.gamercollection.extensions.clearErrors
import es.upsa.mimo.gamercollection.extensions.onFocusChange
import es.upsa.mimo.gamercollection.utils.Constants
import es.upsa.mimo.gamercollection.viewmodelfactories.RegisterViewModelFactory
import es.upsa.mimo.gamercollection.viewmodels.RegisterViewModel

class RegisterFragment : BindingFragment<FragmentRegisterBinding>() {

    //region Private properties
    private lateinit var viewModel: RegisterViewModel
    //endregion

    //region Lifecycle methods
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeUI()
    }
    //endregion

    //region Private methods
    private fun initializeUI() {

        val application = activity?.application
        viewModel = ViewModelProvider(
            this,
            RegisterViewModelFactory(application)
        ).get(RegisterViewModel::class.java)
        setupBindings()

        with(binding) {

            editTextUser.afterTextChanged {
                registerDataChanged()
            }
            editTextUser.onFocusChange {
                registerDataChanged()
            }

            imageButtonInfo.setOnClickListener {
                showPopupDialog(resources.getString(R.string.username_info))
            }

            editTextPassword.afterTextChanged {
                registerDataChanged()
            }
            editTextPassword.onFocusChange {
                registerDataChanged()
            }

            imageButtonPassword.setOnClickListener {
                Constants.showOrHidePassword(
                    editTextPassword,
                    imageButtonPassword,
                    Constants.isDarkMode(context)
                )
            }

            editTextRepeatPassword.afterTextChanged {
                registerDataChanged()
            }
            editTextRepeatPassword.onFocusChange {
                registerDataChanged()
            }

            imageButtonConfirmPassword.setOnClickListener {
                Constants.showOrHidePassword(
                    editTextRepeatPassword,
                    imageButtonConfirmPassword,
                    Constants.isDarkMode(context)
                )
            }

            registerButton.setOnClickListener {
                register()
            }
        }
    }

    private fun setupBindings() {

        viewModel.registerFormState.observe(viewLifecycleOwner, {

            val registerState = it ?: return@observe

            with(binding) {

                editTextUser.clearErrors()
                editTextPassword.clearErrors()
                editTextRepeatPassword.clearErrors()

                registerButton.isEnabled = registerState.isDataValid

                if (registerState.usernameError != null) {
                    editTextUser.error = getString(registerState.usernameError)
                }
                if (registerState.passwordError != null) {

                    editTextPassword.error = getString(registerState.passwordError)
                    editTextRepeatPassword.error = getString(registerState.passwordError)
                }
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
            binding.editTextUser.text.toString(),
            binding.editTextPassword.text.toString(),
            binding.editTextRepeatPassword.text.toString()
        )
    }

    private fun register() {

        val username = binding.editTextUser.text.toString()
        val password = binding.editTextPassword.text.toString()
        val repeatPassword = binding.editTextRepeatPassword.text.toString()

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
    //endregion
}
