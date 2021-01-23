package es.upsa.mimo.gamercollection.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import es.upsa.mimo.gamercollection.R
import es.upsa.mimo.gamercollection.activities.MainActivity
import es.upsa.mimo.gamercollection.fragments.base.BaseFragment
import es.upsa.mimo.gamercollection.injection.GamerCollectionApplication
import es.upsa.mimo.gamercollection.models.*
import es.upsa.mimo.gamercollection.network.apiClient.*
import es.upsa.mimo.gamercollection.utils.Constants
import es.upsa.mimo.gamercollection.utils.SharedPreferencesHandler
import kotlinx.android.synthetic.main.fragment_register.*
import javax.inject.Inject

class RegisterFragment : BaseFragment() {

    @Inject
    lateinit var sharedPrefHandler: SharedPreferencesHandler
    @Inject
    lateinit var formatAPIClient: FormatAPIClient
    @Inject
    lateinit var genreAPIClient: GenreAPIClient
    @Inject
    lateinit var platformAPIClient: PlatformAPIClient
    @Inject
    lateinit var stateAPIClient: StateAPIClient
    @Inject
    lateinit var userAPIClient: UserAPIClient

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_register, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val application = activity?.application
        (application as GamerCollectionApplication).appComponent.inject(this)

        initializeUI()
    }

    //MARK: - Private functions

    private fun initializeUI() {
        register_button.setOnClickListener {register()}
    }

    private fun register() {

        val username = edit_text_user.text.toString()
        val password = edit_text_password.text.toString()
        val repeatPassword = edit_text_repeatPassword.text.toString()

        if (username.isEmpty() || password.isEmpty() || repeatPassword.isEmpty()) {
            showPopupDialog(resources.getString(R.string.error_registration_empty_data))
            return
        }

        if (password != repeatPassword) {
            showPopupDialog(resources.getString(R.string.error_registration_different_passwords))
            return
        }

        showLoading()
        userAPIClient.register(username, password, {
            userAPIClient.login(username, password, { token ->

                val userData = UserData(username, password, false)
                val authData = AuthData(token)
                sharedPrefHandler.run {
                    storeUserData(userData)
                    storeCredentials(authData)
                }
                syncApp(userData)
            }, {
                manageError(it)
            })
        }, {
            manageError(it)
        })
    }

    private fun syncApp(userData: UserData) {

        formatAPIClient.getFormats({ formats ->
            genreAPIClient.getGenres({ genres ->
                platformAPIClient.getPlatforms({ platforms ->
                    stateAPIClient.getStates({ states ->

                        Constants.manageFormats(requireContext(), formats)
                        Constants.manageGenres(requireContext(), genres)
                        Constants.managePlatforms(requireContext(), platforms)
                        Constants.manageStates(requireContext(), states)

                        userData.isLoggedIn = true
                        sharedPrefHandler.storeUserData(userData)
                        goToMainView()
                        hideLoading()
                    }, {
                        manageError(it)
                    })
                }, {
                    manageError(it)
                })
            }, {
                manageError(it)
            })
        }, {
            manageError(it)
        })
    }

    private fun goToMainView() {
        launchActivity(MainActivity::class.java)
    }
}
