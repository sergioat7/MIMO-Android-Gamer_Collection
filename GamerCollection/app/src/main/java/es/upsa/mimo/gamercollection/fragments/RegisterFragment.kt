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
import es.upsa.mimo.gamercollection.utils.StatusBarStyle
import es.upsa.mimo.gamercollection.viewmodelfactories.RegisterViewModelFactory
import es.upsa.mimo.gamercollection.viewmodels.RegisterViewModel

class RegisterFragment : BindingFragment<FragmentRegisterBinding>() {

    //region Protected properties
    override val statusBarStyle = StatusBarStyle.SECONDARY
    //endregion

    //region Private properties
    private lateinit var viewModel: RegisterViewModel
    //endregion

    //region Lifecycle methods
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeUI()
    }
    //endregion

    //region Public methods
    fun register() {

        viewModel.register(
            binding.editTextUser.text.toString(),
            binding.editTextPassword.text.toString()
        )
    }
    //endregion

    //region Private methods
    private fun initializeUI() {

        val application = activity?.application
        viewModel = ViewModelProvider(
            this,
            RegisterViewModelFactory(application)
        )[RegisterViewModel::class.java]
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
                    imageButtonPassword
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
                    imageButtonConfirmPassword
                )
            }

            fragment = this@RegisterFragment
            viewModel = this@RegisterFragment.viewModel
            lifecycleOwner = this@RegisterFragment
        }
    }

    private fun setupBindings() {

        viewModel.registerFormState.observe(viewLifecycleOwner) {

            val registerState = it ?: return@observe

            with(binding) {

                editTextUser.clearErrors()
                editTextPassword.clearErrors()
                editTextRepeatPassword.clearErrors()

                if (registerState.usernameError != null) {
                    editTextUser.error = getString(registerState.usernameError)
                }
                if (registerState.passwordError != null) {

                    editTextPassword.error = getString(registerState.passwordError)
                    editTextRepeatPassword.error = getString(registerState.passwordError)
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
            binding.editTextUser.text.toString(),
            binding.editTextPassword.text.toString(),
            binding.editTextRepeatPassword.text.toString()
        )
    }
    //endregion
}
