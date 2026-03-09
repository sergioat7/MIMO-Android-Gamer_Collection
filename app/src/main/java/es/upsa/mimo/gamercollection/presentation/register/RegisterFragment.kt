package es.upsa.mimo.gamercollection.presentation.register

import android.os.Bundle
import android.view.View
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import es.upsa.mimo.gamercollection.R
import es.upsa.mimo.gamercollection.databinding.FragmentRegisterBinding
import es.upsa.mimo.gamercollection.extensions.doAfterTextChanged
import es.upsa.mimo.gamercollection.extensions.getValue
import es.upsa.mimo.gamercollection.extensions.setEndIconOnClickListener
import es.upsa.mimo.gamercollection.extensions.setError
import es.upsa.mimo.gamercollection.presentation.MainActivity
import es.upsa.mimo.gamercollection.presentation.base.BindingFragment
import es.upsa.mimo.gamercollection.utils.StatusBarStyle
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RegisterFragment : BindingFragment<FragmentRegisterBinding>() {

    //region Protected properties
    override val statusBarStyle = StatusBarStyle.PRIMARY
    override val hasOptionsMenu = false
    //endregion

    //region Private properties
    private val viewModel: RegisterViewModel by viewModels()
    //endregion

    //region Lifecycle methods
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeUi()
    }

    override fun onResume() {
        super.onResume()

        binding.textInputLayoutUsername.doAfterTextChanged {
            registerDataChanged()
        }
        binding.textInputLayoutPassword.doAfterTextChanged {
            registerDataChanged()
        }
        binding.textInputLayoutConfirmPassword.doAfterTextChanged {
            registerDataChanged()
        }
    }
    //endregion

    //region Public methods
    fun register() {
        binding.textInputLayoutUsername.textInputEditText.clearFocus()
        binding.textInputLayoutPassword.textInputEditText.clearFocus()
        binding.textInputLayoutConfirmPassword.textInputEditText.clearFocus()
        viewModel.register(
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

        binding.textInputLayoutUsername.setEndIconOnClickListener {
            showPopupDialog(resources.getString(R.string.username_info))
        }
        binding.fragment = this
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
    }
    //endregion

    //region Private methods
    private fun setupBindings() {
        lifecycleScope.launch {
            viewModel.registerFormState.filterNotNull().collect { registerState ->

                with(binding) {
                    textInputLayoutUsername.setError("")
                    textInputLayoutPassword.setError("")
                    textInputLayoutConfirmPassword.setError("")

                    if (registerState.usernameError != null) {
                        textInputLayoutUsername.setError(getString(registerState.usernameError))
                    }
                    if (registerState.passwordError != null) {
                        textInputLayoutPassword.setError(getString(registerState.passwordError))
                        textInputLayoutConfirmPassword.setError(
                            getString(registerState.passwordError),
                        )
                    }
                }
            }
        }

        lifecycleScope.launch {
            viewModel.registerLoading.filterNotNull().collect { isLoading ->

                if (isLoading) {
                    showLoading()
                } else {
                    hideLoading()
                }
            }
        }

        lifecycleScope.launch {
            viewModel.registerError.filterNotNull().collect { error ->

                hideLoading()
                manageError(error)
            }
        }

        lifecycleScope.launch {
            viewModel.registerSuccess.filterNotNull().collect { success ->

                if (success) {
                    launchActivity(MainActivity::class.java)
                }
            }
        }
    }

    private fun registerDataChanged() {
        viewModel.registerDataChanged(
            binding.textInputLayoutUsername.getValue(),
            binding.textInputLayoutPassword.getValue(),
            binding.textInputLayoutConfirmPassword.getValue(),
        )
    }
    //endregion
}
