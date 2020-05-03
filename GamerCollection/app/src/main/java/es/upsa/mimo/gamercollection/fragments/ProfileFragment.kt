package es.upsa.mimo.gamercollection.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import es.upsa.mimo.gamercollection.R
import es.upsa.mimo.gamercollection.fragments.base.BaseFragment
import es.upsa.mimo.gamercollection.models.AuthData
import es.upsa.mimo.gamercollection.network.apiClient.UserAPIClient
import es.upsa.mimo.gamercollection.utils.SharedPreferencesHandler
import kotlinx.android.synthetic.main.fragment_profile.*

class ProfileFragment : BaseFragment() {

    private lateinit var sharedPrefHandler: SharedPreferencesHandler
    private lateinit var userAPIClient: UserAPIClient

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedPrefHandler = SharedPreferencesHandler(context)
        userAPIClient = UserAPIClient(resources, sharedPrefHandler)

        initializeUI()
    }

    //MARK: - Private functions

    private fun initializeUI() {

        val userData = sharedPrefHandler.getUserData()
        edit_text_user.setText(userData.username)
        edit_text_password.setText(userData.password)

        change_password_button.setOnClickListener {updatePassword()}
        delete_user_button.setOnClickListener {deleteUser()}
    }

    private fun updatePassword() {

        val newPassword = edit_text_password.text.toString()

        showLoading(view?.parent as View)
        userAPIClient.updatePassword(newPassword, {
            sharedPrefHandler.storePassword(newPassword)
            val userData = sharedPrefHandler.getUserData()
            userAPIClient.login(userData.username, userData.password, {

                val authData = AuthData(it)
                sharedPrefHandler.storeCredentials(authData)
                hideLoading()
            }, {
                manageError(it)
            })
        }, {
            manageError(it)
        })
    }

    private fun deleteUser() {
    }
}
