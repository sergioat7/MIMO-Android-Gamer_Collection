package es.upsa.mimo.gamercollection.fragments

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import es.upsa.mimo.gamercollection.activities.MainActivity
import es.upsa.mimo.gamercollection.activities.RegisterActivity
import es.upsa.mimo.gamercollection.databinding.FragmentLoginBinding
import es.upsa.mimo.gamercollection.extensions.afterTextChanged
import es.upsa.mimo.gamercollection.extensions.onFocusChange
import es.upsa.mimo.gamercollection.fragments.base.BindingFragment
import es.upsa.mimo.gamercollection.utils.Constants
import es.upsa.mimo.gamercollection.utils.Environment
import es.upsa.mimo.gamercollection.viewmodelfactories.LoginViewModelFactory
import es.upsa.mimo.gamercollection.viewmodels.LoginViewModel

class LoginFragment : BindingFragment<FragmentLoginBinding>() {

    //MARK: - Private properties

    private lateinit var viewModel: LoginViewModel

    // MARK: - Lifecycle methods

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeUI()
    }

    //MARK: - Private methods

    private fun initializeUI() {

        val application = activity?.application
        viewModel = ViewModelProvider(
            this,
            LoginViewModelFactory(application)
        ).get(LoginViewModel::class.java)
        setupBindings()

        val username = viewModel.username
        val user = if (username.isEmpty()) Environment.getUsername() else username
        val password = if (username.isEmpty()) Environment.getPassword() else Constants.EMPTY_VALUE

        with(binding) {

            editTextUser.setText(user)
            editTextUser.afterTextChanged {
                loginDataChanged()
            }
            editTextUser.onFocusChange {
                loginDataChanged()
            }

            editTextPassword.setText(password)
            editTextPassword.afterTextChanged {
                loginDataChanged()
            }
            editTextPassword.onFocusChange {
                loginDataChanged()
            }

            imageButtonPassword.setOnClickListener {
                Constants.showOrHidePassword(
                    editTextPassword,
                    imageButtonPassword,
                    Constants.isDarkMode(context)
                )
            }

            loginButton.setOnClickListener {

                viewModel.login(
                    editTextUser.text.toString(),
                    editTextPassword.text.toString()
                )
            }

            registerButton.setOnClickListener {
                launchActivity(RegisterActivity::class.java)
            }
        }
    }

    private fun setupBindings() {

        viewModel.loginFormState.observe(viewLifecycleOwner, {

            val loginState = it ?: return@observe

            binding.loginButton.isEnabled = loginState.isDataValid

            if (loginState.usernameError != null) {
                binding.editTextUser.error = getString(loginState.usernameError)
            }
            if (loginState.passwordError != null) {
                binding.editTextPassword.error = getString(loginState.passwordError)
            }
        })

        viewModel.loginLoading.observe(viewLifecycleOwner, { isLoading ->

            if (isLoading) {
                showLoading()
            } else {
                hideLoading()
            }
        })

        viewModel.loginError.observe(viewLifecycleOwner, { error ->

            if (error == null) {
                launchActivity(MainActivity::class.java)
            } else {

                hideLoading()
                manageError(error)
            }
        })
    }

    private fun loginDataChanged() {

        viewModel.loginDataChanged(
            binding.editTextUser.text.toString(),
            binding.editTextPassword.text.toString()
        )
    }
}
