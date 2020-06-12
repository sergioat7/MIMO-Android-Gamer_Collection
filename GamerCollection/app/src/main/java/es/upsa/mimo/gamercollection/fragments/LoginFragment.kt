package es.upsa.mimo.gamercollection.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import es.upsa.mimo.gamercollection.R
import es.upsa.mimo.gamercollection.activities.MainActivity
import es.upsa.mimo.gamercollection.activities.RegisterActivity
import es.upsa.mimo.gamercollection.fragments.base.BaseFragment
import es.upsa.mimo.gamercollection.models.*
import es.upsa.mimo.gamercollection.network.apiClient.*
import es.upsa.mimo.gamercollection.persistence.repositories.*
import es.upsa.mimo.gamercollection.utils.SharedPreferencesHandler
import kotlinx.android.synthetic.main.fragment_login.*

class LoginFragment : BaseFragment() {

    private lateinit var sharedPrefHandler: SharedPreferencesHandler
    private lateinit var formatAPIClient: FormatAPIClient
    private lateinit var genreAPIClient: GenreAPIClient
    private lateinit var platformAPIClient: PlatformAPIClient
    private lateinit var stateAPIClient: StateAPIClient
    private lateinit var gameAPIClient: GameAPIClient
    private lateinit var sagaAPIClient: SagaAPIClient
    private lateinit var userAPIClient: UserAPIClient
    private lateinit var formatRepository: FormatRepository
    private lateinit var genreRepository: GenreRepository
    private lateinit var platformRepository: PlatformRepository
    private lateinit var stateRepository: StateRepository
    private lateinit var gameRepository: GameRepository
    private lateinit var sagaRepository: SagaRepository

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedPrefHandler = SharedPreferencesHandler(context)
        formatAPIClient = FormatAPIClient(resources, sharedPrefHandler)
        genreAPIClient = GenreAPIClient(resources, sharedPrefHandler)
        platformAPIClient = PlatformAPIClient(resources, sharedPrefHandler)
        stateAPIClient = StateAPIClient(resources, sharedPrefHandler)
        gameAPIClient = GameAPIClient(resources, sharedPrefHandler)
        sagaAPIClient = SagaAPIClient(resources, sharedPrefHandler)
        userAPIClient = UserAPIClient(resources, sharedPrefHandler)
        formatRepository = FormatRepository(requireContext())
        genreRepository = GenreRepository(requireContext())
        platformRepository = PlatformRepository(requireContext())
        stateRepository = StateRepository(requireContext())
        gameRepository = GameRepository(requireContext())
        sagaRepository = SagaRepository(requireContext())

        initializeUI()
    }

    //MARK: - Private functions

    private fun initializeUI() {

        val username = sharedPrefHandler.getUserData().username
        edit_text_user.setText(username)

        login_button.setOnClickListener {login()}
        register_button.setOnClickListener {register()}
    }

    private fun login() {

        val username = edit_text_user.text.toString()
        val password = edit_text_password.text.toString()

        if (username.isEmpty() || password.isEmpty()) {
            showPopupDialog(resources.getString(R.string.ERROR_REGISTRATION_EMPTY_DATA))
            return
        }

        showLoading()
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
    }

    private fun register() {
        launchActivity(RegisterActivity::class.java)
    }

    private fun syncApp(userData: UserData) {

        formatAPIClient.getFormats({ formats ->
            genreAPIClient.getGenres({ genres ->
                platformAPIClient.getPlatforms({ platforms ->
                    stateAPIClient.getStates({ states ->
                        gameAPIClient.getGames({ games ->
                            sagaAPIClient.getSagas({ sagas ->

                                manageFormats(formats)
                                manageGenres(genres)
                                managePlatforms(platforms)
                                manageStates(states)
                                manageGames(games)
                                manageSagas(sagas)

                                userData.isLoggedIn = true
                                sharedPrefHandler.storeUserData(userData)
                                launchActivity(MainActivity::class.java)
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
            }, {
                manageError(it)
            })
        }, {
            manageError(it)
        })
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

    private fun manageGames(games: List<GameResponse>) {

        for (game in games) {
            gameRepository.insertGame(game)
        }
        gameRepository.removeDisableContent(games)
    }

    private fun manageSagas(sagas: List<SagaResponse>) {

        for (saga in sagas) {
            sagaRepository.insertSaga(saga)
        }
        sagaRepository.removeDisableContent(sagas)
    }
}
