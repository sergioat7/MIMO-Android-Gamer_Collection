package es.upsa.mimo.gamercollection.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import es.upsa.mimo.gamercollection.R
import es.upsa.mimo.gamercollection.activities.MainActivity
import es.upsa.mimo.gamercollection.activities.base.BaseFragment
import es.upsa.mimo.gamercollection.models.AuthData
import es.upsa.mimo.gamercollection.models.UserData
import es.upsa.mimo.gamercollection.network.apiClient.LoginAPIClient
import es.upsa.mimo.gamercollection.utils.SharedPreferencesHandler
import kotlinx.android.synthetic.main.fragment_login.*


class LoginFragment : BaseFragment() {

    private lateinit var sharedPrefHandler: SharedPreferencesHandler

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedPrefHandler = SharedPreferencesHandler(context)
        checkIsNewInstallation()
        showMainView()
    }

    //MARK: - Private functions

    private fun checkIsNewInstallation() {

        if (sharedPrefHandler.isNewInstallation()) {
            sharedPrefHandler.removeUserData()
            sharedPrefHandler.removeCredentials()
            sharedPrefHandler.setIsNewInstallation()
        }
    }

    private fun showMainView() {

        if (sharedPrefHandler.isLoggedIn()) {
            val intent = Intent(context, MainActivity::class.java).apply {}
            startActivity(intent)
        } else {
            initializeUI()
        }
    }

    private fun initializeUI() {

        val username = sharedPrefHandler.getUserData().username
        editTextEmail.setText(username)

        login_button.setOnClickListener {login()}
        register_button.setOnClickListener {register()}
    }

    private fun login() {

        val username = editTextEmail.text.toString()
        val password = editTextPassword.text.toString()

        showLoading(view)
        LoginAPIClient.login(username, password, resources, { token ->

            val userData = UserData(username, password, true)
            val authData = AuthData(token)
            sharedPrefHandler.run {
                storeUserData(userData)
                storeCredentials(authData)
            }
            syncApp()
        }, { errorResponse ->

            hideLoading()
            showPopupDialog(errorResponse.error)
        })
    }

    private fun register() {
        //TODO go to register view
    }

    private fun syncApp() {

        //TODO get formats
        //TODO get genres
        //TODO get platforms
        //TODO get states
        //TODO get games
        //TODO get sagas
        val intent = Intent(context, MainActivity::class.java).apply {}
        startActivity(intent)
        hideLoading()
    }
}
