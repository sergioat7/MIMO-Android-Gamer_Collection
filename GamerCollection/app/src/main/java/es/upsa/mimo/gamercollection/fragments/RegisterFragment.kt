package es.upsa.mimo.gamercollection.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import es.upsa.mimo.gamercollection.R
import es.upsa.mimo.gamercollection.activities.MainActivity
import es.upsa.mimo.gamercollection.fragments.base.BaseFragment
import es.upsa.mimo.gamercollection.models.*
import es.upsa.mimo.gamercollection.network.apiClient.*
import es.upsa.mimo.gamercollection.persistence.repositories.*
import es.upsa.mimo.gamercollection.utils.SharedPreferencesHandler
import kotlinx.android.synthetic.main.fragment_register.*

class RegisterFragment : BaseFragment() {

    private lateinit var sharedPrefHandler: SharedPreferencesHandler
    private lateinit var formatAPIClient: FormatAPIClient
    private lateinit var genreAPIClient: GenreAPIClient
    private lateinit var platformAPIClient: PlatformAPIClient
    private lateinit var stateAPIClient: StateAPIClient
    private lateinit var userAPIClient: UserAPIClient
    private lateinit var formatRepository: FormatRepository
    private lateinit var genreRepository: GenreRepository
    private lateinit var platformRepository: PlatformRepository
    private lateinit var stateRepository: StateRepository

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_register, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedPrefHandler = SharedPreferencesHandler(context)
        formatAPIClient = FormatAPIClient(resources)
        genreAPIClient = GenreAPIClient(resources)
        platformAPIClient = PlatformAPIClient(resources)
        stateAPIClient = StateAPIClient(resources)
        userAPIClient = UserAPIClient(resources, sharedPrefHandler)
        formatRepository = FormatRepository(requireContext())
        genreRepository = GenreRepository(requireContext())
        platformRepository = PlatformRepository(requireContext())
        stateRepository = StateRepository(requireContext())

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
            showPopupDialog(resources.getString(R.string.ERROR_REGISTRATION_EMPTY_DATA))
            return
        }

        if (password != repeatPassword) {
            showPopupDialog(resources.getString(R.string.ERROR_REGISTRATION_DIFFERENT_PASSWORDS))
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

                        manageFormats(formats)
                        manageGenres(genres)
                        managePlatforms(platforms)
                        manageStates(states)

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

    private fun manageFormats(formats: List<FormatResponse>) {

        for (format in formats) {
            formatRepository.insertFormat(format)
        }
        formatRepository.removeDisableContent(formats)
    }

    private fun manageGenres(genres: List<GenreResponse>) {

        for (genre in genres) {
            genreRepository.insertGenre(genre)
        }
        genreRepository.removeDisableContent(genres)
    }

    private fun managePlatforms(platforms: List<PlatformResponse>) {

        for (platform in platforms) {
            platformRepository.insertPlatform(platform)
        }
        platformRepository.removeDisableContent(platforms)
    }

    private fun manageStates(states: List<StateResponse>) {

        for (state in states) {
            stateRepository.insertState(state)
        }
        stateRepository.removeDisableContent(states)
    }
}
