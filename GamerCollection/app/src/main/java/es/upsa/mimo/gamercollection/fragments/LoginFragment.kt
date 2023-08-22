package es.upsa.mimo.gamercollection.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import es.upsa.mimo.gamercollection.activities.MainActivity
import es.upsa.mimo.gamercollection.activities.RegisterActivity
import es.upsa.mimo.gamercollection.base.BindingFragment
import es.upsa.mimo.gamercollection.databinding.FragmentLoginBinding
import es.upsa.mimo.gamercollection.extensions.doAfterTextChanged
import es.upsa.mimo.gamercollection.extensions.getValue
import es.upsa.mimo.gamercollection.extensions.setError
import es.upsa.mimo.gamercollection.utils.StatusBarStyle
import es.upsa.mimo.gamercollection.viewmodels.LoginViewModel

class LoginFragment : BindingFragment<FragmentLoginBinding>() {

    //region Protected properties
    override val statusBarStyle = StatusBarStyle.SECONDARY
    override val hasOptionsMenu = false
    //endregion

    //region Private properties
    private val viewModel: LoginViewModel by viewModels()
    //endregion

    //region Lifecycle methods
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeUi()
    }

    override fun onResume() {
        super.onResume()

        binding.textInputLayoutUsername.doAfterTextChanged {
            loginDataChanged()
        }
        binding.textInputLayoutPassword.doAfterTextChanged {
            loginDataChanged()
        }
    }
    //endregion

    //region Public methods
    fun goToRegister() {
        launchActivity(RegisterActivity::class.java)
    }

    fun login() {

        binding.textInputLayoutUsername.textInputEditText.clearFocus()
        binding.textInputLayoutPassword.textInputEditText.clearFocus()
        viewModel.login(
            binding.textInputLayoutUsername.getValue(),
            binding.textInputLayoutPassword.getValue()
        )
    }
    //endregion

    //region Protected methods
    override fun initializeUi() {
        super.initializeUi()

        setupBindings()

        binding.fragment = this
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
    }
    //endregion

    //region Private methods
    private fun setupBindings() {

        viewModel.loginFormState.observe(viewLifecycleOwner) {

            binding.textInputLayoutUsername.setError("")
            binding.textInputLayoutPassword.setError("")
            val loginState = it ?: return@observe

            if (loginState.usernameError != null) {
                binding.textInputLayoutUsername.setError(getString(loginState.usernameError))
            }
            if (loginState.passwordError != null) {
                binding.textInputLayoutPassword.setError(getString(loginState.passwordError))
            }
        }

        viewModel.loginLoading.observe(viewLifecycleOwner) { isLoading ->

            if (isLoading) {
                showLoading()
            } else {
                hideLoading()
            }
        }

        viewModel.loginError.observe(viewLifecycleOwner) { error ->

            if (error == null) {
                launchActivity(MainActivity::class.java, true)
            } else {

                hideLoading()
                manageError(error)
            }
        }
    }

    private fun loginDataChanged() {

        viewModel.loginDataChanged(
            binding.textInputLayoutUsername.getValue(),
            binding.textInputLayoutPassword.getValue()
        )
    }
    //endregion
}
