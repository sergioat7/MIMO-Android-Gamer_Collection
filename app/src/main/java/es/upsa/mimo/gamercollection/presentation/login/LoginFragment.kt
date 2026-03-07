package es.upsa.mimo.gamercollection.presentation.login

import android.os.Bundle
import android.view.View
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import es.upsa.mimo.gamercollection.databinding.FragmentLoginBinding
import es.upsa.mimo.gamercollection.extensions.doAfterTextChanged
import es.upsa.mimo.gamercollection.extensions.getValue
import es.upsa.mimo.gamercollection.extensions.setError
import es.upsa.mimo.gamercollection.presentation.MainActivity
import es.upsa.mimo.gamercollection.presentation.base.BindingFragment
import es.upsa.mimo.gamercollection.presentation.register.RegisterActivity
import es.upsa.mimo.gamercollection.utils.StatusBarStyle
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginFragment : BindingFragment<FragmentLoginBinding>() {

    //region Protected properties
    override val statusBarStyle = StatusBarStyle.PRIMARY
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
            binding.textInputLayoutPassword.getValue(),
        )
    }
    //endregion

    //region Protected methods
    override fun initializeUi() {
        super.initializeUi()

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.updatePadding(bottom = insets.bottom)
            windowInsets
        }

        setupBindings()

        binding.fragment = this
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
    }
    //endregion

    //region Private methods
    private fun setupBindings() {
        lifecycleScope.launch {
            viewModel.loginFormState.filterNotNull().collect { loginState ->

                binding.textInputLayoutUsername.setError("")
                binding.textInputLayoutPassword.setError("")

                if (loginState.usernameError != null) {
                    binding.textInputLayoutUsername.setError(getString(loginState.usernameError))
                }
                if (loginState.passwordError != null) {
                    binding.textInputLayoutPassword.setError(getString(loginState.passwordError))
                }
            }
        }

        lifecycleScope.launch {
            viewModel.loginLoading.filterNotNull().collect { isLoading ->

                if (isLoading) {
                    showLoading()
                } else {
                    hideLoading()
                }
            }
        }

        lifecycleScope.launch {
            viewModel.loginError.filterNotNull().collect { error ->

                hideLoading()
                manageError(error)
            }
        }

        lifecycleScope.launch {
            viewModel.loginSuccess.filterNotNull().collect { success ->

                if (success) {
                    launchActivity(MainActivity::class.java, true)
                }
            }
        }
    }

    private fun loginDataChanged() {
        viewModel.loginDataChanged(
            binding.textInputLayoutUsername.getValue(),
            binding.textInputLayoutPassword.getValue(),
        )
    }
    //endregion
}
