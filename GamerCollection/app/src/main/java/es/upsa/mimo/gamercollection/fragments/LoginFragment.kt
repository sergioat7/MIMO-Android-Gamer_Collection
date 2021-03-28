package es.upsa.mimo.gamercollection.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import es.upsa.mimo.gamercollection.R
import es.upsa.mimo.gamercollection.activities.MainActivity
import es.upsa.mimo.gamercollection.activities.RegisterActivity
import es.upsa.mimo.gamercollection.extensions.afterTextChanged
import es.upsa.mimo.gamercollection.extensions.onFocusChange
import es.upsa.mimo.gamercollection.fragments.base.BaseFragment
import es.upsa.mimo.gamercollection.utils.Constants
import es.upsa.mimo.gamercollection.utils.Environment
import es.upsa.mimo.gamercollection.viewmodelfactories.LoginViewModelFactory
import es.upsa.mimo.gamercollection.viewmodels.LoginViewModel
import kotlinx.android.synthetic.main.fragment_login.*

class LoginFragment : BaseFragment() {

    //MARK: - Private properties

    private lateinit var viewModel: LoginViewModel

    // MARK: - Lifecycle methods

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

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
        edit_text_user.setText(user)
        val password = if (username.isEmpty()) Environment.getPassword() else Constants.EMPTY_VALUE
        edit_text_password.setText(password)

        edit_text_user.afterTextChanged {
            loginDataChanged()
        }
        edit_text_user.onFocusChange {
            loginDataChanged()
        }

        edit_text_password.afterTextChanged {
            loginDataChanged()
        }
        edit_text_password.onFocusChange {
            loginDataChanged()
        }

        image_button_password.setOnClickListener {
            Constants.showOrHidePassword(
                edit_text_password,
                image_button_password,
                Constants.isDarkMode(context)
            )
        }

        login_button.setOnClickListener {

            viewModel.login(
                edit_text_user.text.toString(),
                edit_text_password.text.toString()
            )
        }
        register_button.setOnClickListener {
            launchActivity(RegisterActivity::class.java)
        }
    }

    private fun setupBindings() {

        viewModel.loginFormState.observe(viewLifecycleOwner, {

            val loginState = it ?: return@observe

            login_button.isEnabled = loginState.isDataValid

            if (loginState.usernameError != null) {
                edit_text_user.error = getString(loginState.usernameError)
            }
            if (loginState.passwordError != null) {
                edit_text_password.error = getString(loginState.passwordError)
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
            edit_text_user.text.toString(),
            edit_text_password.text.toString()
        )
    }
}
