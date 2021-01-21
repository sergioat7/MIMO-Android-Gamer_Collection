package es.upsa.mimo.gamercollection.fragments

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.text.InputType
import android.view.*
import androidx.core.content.ContextCompat
import es.upsa.mimo.gamercollection.R
import es.upsa.mimo.gamercollection.activities.LandingActivity
import es.upsa.mimo.gamercollection.activities.LoginActivity
import es.upsa.mimo.gamercollection.extensions.setReadOnly
import es.upsa.mimo.gamercollection.fragments.base.BaseFragment
import es.upsa.mimo.gamercollection.injection.GamerCollectionApplication
import es.upsa.mimo.gamercollection.models.AuthData
import es.upsa.mimo.gamercollection.network.apiClient.*
import es.upsa.mimo.gamercollection.repositories.GameRepository
import es.upsa.mimo.gamercollection.repositories.SagaRepository
import es.upsa.mimo.gamercollection.utils.Constants
import es.upsa.mimo.gamercollection.utils.SharedPreferencesHandler
import kotlinx.android.synthetic.main.fragment_profile.*
import javax.inject.Inject

class ProfileFragment : BaseFragment() {

    @Inject
    lateinit var sharedPrefHandler: SharedPreferencesHandler
    private lateinit var userAPIClient: UserAPIClient
    @Inject
    lateinit var gameRepository: GameRepository
    @Inject
    lateinit var sagaRepository: SagaRepository
    private lateinit var formatAPIClient: FormatAPIClient
    private lateinit var genreAPIClient: GenreAPIClient
    private lateinit var platformAPIClient: PlatformAPIClient
    private lateinit var stateAPIClient: StateAPIClient

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val application = activity?.application
        (application as GamerCollectionApplication).appComponent.inject(this)

        userAPIClient = UserAPIClient(resources, sharedPrefHandler)
        formatAPIClient = FormatAPIClient(resources, sharedPrefHandler)
        genreAPIClient = GenreAPIClient(resources, sharedPrefHandler)
        platformAPIClient = PlatformAPIClient(resources, sharedPrefHandler)
        stateAPIClient = StateAPIClient(resources, sharedPrefHandler)

        initializeUI()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.clear()
        inflater.inflate(R.menu.profile_toolbar_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(item.itemId) {
            R.id.action_synchronize -> {
                openSyncPopup()
                return true
            }
            R.id.action_logout -> {
                logout()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    //MARK: - Private functions

    private fun initializeUI() {

        val userData = sharedPrefHandler.getUserData()
        edit_text_user.setText(userData.username)
        edit_text_password.setText(userData.password)

        edit_text_user.setReadOnly(true, InputType.TYPE_NULL, 0)

        radio_button_en.isChecked = sharedPrefHandler.getLanguage() == "en"
        radio_button_es.isChecked = sharedPrefHandler.getLanguage() == "es"

        spinner_sorting_keys.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.color2))
        spinner_sorting_keys.adapter = Constants.getAdapter(
            requireContext(),
            resources.getStringArray(R.array.sorting_keys).toList(),
            true
        )
        val sortingKeyIds = resources.getStringArray(R.array.sorting_keys_ids)
        sortingKeyIds.firstOrNull { it == sharedPrefHandler.getSortingKey() }?.let {
            spinner_sorting_keys.setSelection(sortingKeyIds.indexOf(it))
        } ?: run {
            spinner_sorting_keys.setSelection(0)
        }

        switch_swipe_refresh.isChecked = sharedPrefHandler.getSwipeRefresh()

        button_change_password.setOnClickListener { updatePassword() }
        button_delete_user.setOnClickListener { deleteUser() }
    }

    private fun logout() {

        showPopupConfirmationDialog(resources.getString(R.string.profile_logout_confirmation)) {

            showLoading()
            userAPIClient.logout({

                sharedPrefHandler.removePassword()
                removeData()
            }, {

                sharedPrefHandler.removePassword()
                removeData()
            })
        }
    }

    private fun updatePassword() {

        val language = if(radio_button_en.isChecked) "en" else "es"
        val sortingKey = resources.getStringArray(R.array.sorting_keys_ids)[spinner_sorting_keys.selectedItemPosition]
        save(
            edit_text_password.text.toString(),
            language,
            sortingKey,
            switch_swipe_refresh.isChecked
        )
    }

    private fun save (newPassword: String, newLanguage: String, newSortParam: String, newSwipeRefresh: Boolean) {

        val changePassword = newPassword != sharedPrefHandler.getUserData().password
        val changeLanguage = newLanguage != sharedPrefHandler.getLanguage()
        val changeSortParam = newSortParam != sharedPrefHandler.getSortingKey()
        val changeSwipeRefresh = newSwipeRefresh != sharedPrefHandler.getSwipeRefresh()

        if (changePassword) {
            showLoading()
            userAPIClient.updatePassword(newPassword, {

                sharedPrefHandler.storePassword(newPassword)
                val userData = sharedPrefHandler.getUserData()
                userAPIClient.login(userData.username, userData.password, {

                    val authData = AuthData(it)
                    sharedPrefHandler.storeCredentials(authData)
                    hideLoading()
                    if (changeLanguage) {
                        reloadData()
                    }
                }, {
                    manageError(it)
                })
            }, {
                manageError(it)
            })
        }

        if (changeSortParam) {
            sharedPrefHandler.setSortingKey(newSortParam)
        }

        if (changeSwipeRefresh) {
            sharedPrefHandler.setSwipeRefresh(newSwipeRefresh)
        }

        if (changeLanguage) {

            sharedPrefHandler.setLanguage(newLanguage)
            if (!changePassword) {
                reloadData()
            }
        }
    }

    private fun reloadData() {

        showLoading()

        formatAPIClient.getFormats({ formats ->
            genreAPIClient.getGenres({ genres ->
                platformAPIClient.getPlatforms({ platforms ->
                    stateAPIClient.getStates({ states ->

                        Constants.manageFormats(requireContext(), formats)
                        Constants.manageGenres(requireContext(), genres)
                        Constants.managePlatforms(requireContext(), platforms)
                        Constants.manageStates(requireContext(), states)

                        val landing = Intent(requireContext(), LandingActivity::class.java)
                        startActivity(landing)
                        activity?.finish()
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

    private fun deleteUser() {

        showPopupConfirmationDialog(resources.getString(R.string.profile_delete_confirmation)) {

            showLoading()
            userAPIClient.deleteUser({

                sharedPrefHandler.removeUserData()
                removeData()
            }, {
                manageError(it)
            })
        }
    }

    private fun removeData() {

        sharedPrefHandler.removeCredentials()
        val games = gameRepository.getGames()
        for (game in games) {
            gameRepository.deleteGame(game)
        }
        val sagas = sagaRepository.getSagas()
        for (saga in sagas) {
            sagaRepository.deleteSaga(saga)
        }

        hideLoading()
        launchActivity(LoginActivity::class.java)
    }
}
