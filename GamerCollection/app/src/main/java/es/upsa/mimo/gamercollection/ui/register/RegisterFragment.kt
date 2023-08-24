package es.upsa.mimo.gamercollection.ui.register

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import es.upsa.mimo.gamercollection.R
import es.upsa.mimo.gamercollection.ui.MainActivity
import es.upsa.mimo.gamercollection.ui.base.BindingFragment
import es.upsa.mimo.gamercollection.databinding.FragmentRegisterBinding
import es.upsa.mimo.gamercollection.extensions.doAfterTextChanged
import es.upsa.mimo.gamercollection.extensions.getValue
import es.upsa.mimo.gamercollection.extensions.setEndIconOnClickListener
import es.upsa.mimo.gamercollection.extensions.setError
import es.upsa.mimo.gamercollection.utils.StatusBarStyle

@AndroidEntryPoint
class RegisterFragment : BindingFragment<FragmentRegisterBinding>() {

    //region Protected properties
    override val statusBarStyle = StatusBarStyle.SECONDARY
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
            binding.textInputLayoutPassword.getValue()
        )
    }
    //endregion

    //region Protected methods
    override fun initializeUi() {
        super.initializeUi()

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

        viewModel.registerFormState.observe(viewLifecycleOwner) {

            val registerState = it ?: return@observe

            with(binding) {
                textInputLayoutUsername.setError("")
                textInputLayoutPassword.setError("")
                textInputLayoutConfirmPassword.setError("")

                if (registerState.usernameError != null) {
                    textInputLayoutUsername.setError(getString(registerState.usernameError))
                }
                if (registerState.passwordError != null) {

                    textInputLayoutPassword.setError(getString(registerState.passwordError))
                    textInputLayoutConfirmPassword.setError(getString(registerState.passwordError))
                }
            }
        }

        viewModel.registerLoading.observe(viewLifecycleOwner) { isLoading ->

            if (isLoading) {
                showLoading()
            } else {
                hideLoading()
            }
        }

        viewModel.registerError.observe(viewLifecycleOwner) { error ->

            if (error == null) {
                launchActivity(MainActivity::class.java)
            } else {

                hideLoading()
                manageError(error)
            }
        }
    }

    private fun registerDataChanged() {

        viewModel.registerDataChanged(
            binding.textInputLayoutUsername.getValue(),
            binding.textInputLayoutPassword.getValue(),
            binding.textInputLayoutConfirmPassword.getValue()
        )
    }
    //endregion
}
